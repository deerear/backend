package com.deerear.app.service;

import com.deerear.app.domain.*;
import com.deerear.app.dto.*;
import com.deerear.app.repository.LikeRepository;
import com.deerear.app.repository.PostImageRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.deerear.app.util.StaticFiles.saveImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public PostDetailResponseDto getPost(UUID postId) {

        Post post = postRepository.getReferenceById(postId);
        Member member = post.getMember();

        List<PostImageDto> postImageListDto = postImageRepository.findAllByPostId(postId).stream()
                .map(postImage -> PostImageDto.builder()
                                .id(postImage.getId())
                                .url(postImage.getImageUrl())
                                .build())
                .toList();

        Boolean isLike = likeRepository.existsByMemberAndTargetTypeAndTargetId(member, Likeable.TargetType.POST, postId);

        return post.toDto(member, postImageListDto, isLike);
    }

    @Transactional(readOnly = true)
    public PagingResponseDto getPosts(Member member, PagingRequestDto pagingRequestDto) {
        String lastPostId = pagingRequestDto.getKey();
        int size = pagingRequestDto.getSize() != 0 ? pagingRequestDto.getSize() : 10; // 요청된 size로 페이징 설정, 기본 10으로 설정

        PageRequest pageRequest = PageRequest.of(0, size);

        List<Post> posts;
        try {
            posts = postRepository.findNextPage(member, pageRequest);
        } catch (IllegalArgumentException e){
            // 커서를 기준으로 이후의 게시물 조회
            posts = postRepository.findNextPage(member, UUID.fromString(lastPostId), pageRequest);
        }

        boolean hasNext = posts.size() == size; // 다음 페이지 여부 확인

        List<Object> postDtos = posts.stream()
                .map(post -> MemberGetPostsResponseDto.toDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt()))
                .collect(Collectors.toList());

        String nextKey = hasNext ? posts.get(posts.size() - 1).getId().toString() : null;

        return new PagingResponseDto(postDtos, size, nextKey, hasNext);
    }

    @Transactional(readOnly = true)
    public PagingResponseDto listPosts(CustomUserDetails customUserDetails, PostListRequestDto postListRequestDto, String key, Integer size){

        Member member = customUserDetails.getUser();

        List<Post> posts;

        try {
            UUID postId = UUID.fromString(key);
            Post post = postRepository.getReferenceById(postId);
            posts = postRepository.findNextPage(post.getCreatedAt(), post.getId(), postListRequestDto.getStartLatitude(), postListRequestDto.getStartLongitude(), postListRequestDto.getEndLatitude(), postListRequestDto.getEndLongitude(), Pageable.ofSize(size+1));
        } catch (IllegalArgumentException e) {
            posts = postRepository.findNextPage(postListRequestDto.getStartLatitude(), postListRequestDto.getStartLongitude(), postListRequestDto.getEndLatitude(), postListRequestDto.getEndLongitude(), Pageable.ofSize(size+1));
        }

        String nextKey = "";
        boolean hasNext = false;

        if (posts.size() == 11){
            posts = posts.subList(0,10);
            hasNext = true;
            nextKey = posts.get(posts.size()-1).getId().toString();
        }

        List<PostDto> postListDto = posts.stream()
                .map(post -> PostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .thumbnail(post.getThumbnail())
                        .commentCount(post.getCommentCount())
                        .isLike(likeRepository.existsByMemberAndTargetTypeAndTargetId(member, Likeable.TargetType.POST, post.getId()))
                        .likesCount(post.getLikeCount())
                        .latitude(post.getLatitude())
                        .longitude(post.getLongitude())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();


        return PagingResponseDto.builder()
                .objects(Collections.singletonList(postListDto))
                .hasNext(hasNext)
                .key(nextKey)
                .size(postListDto.size())
                .build();
    }

    @Transactional
    public void createPost(CustomUserDetails customUserDetails, PostCreateRequestDto postCreateRequestDto) {

        String thumbnail = "";
        List<MultipartFile> images = postCreateRequestDto.getPostImgs();

        if (images.size() > 0) {
            MultipartFile firstImage = images.get(0);
            thumbnail = saveImage(firstImage, "posts","thumbnails",true);
        }


        Post post = postRepository.save(postCreateRequestDto.toEntity(customUserDetails.getUser(), thumbnail));

        if (images.size() > 0) {
            List<PostImage> postImages = new ArrayList<>();
            for(MultipartFile image: postCreateRequestDto.getPostImgs()){
                String path = saveImage(image, "posts", post.getId().toString(), false);
                PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
                postImages.add(postImage);
            }
            postImageRepository.saveAll(postImages);
        }
    }

    @Transactional
    public void updatePost(CustomUserDetails customUserDetails, UUID postId, PostUpdateRequestDto postUpdateRequestDto) {

        Member member = customUserDetails.getUser();

        validate(member, postUpdateRequestDto.toEntity(member));

        Post post = postRepository.getReferenceById(postId);

        if (postUpdateRequestDto.getPostImgs() != null){
            for(MultipartFile image: postUpdateRequestDto.getPostImgs()){
                String path = saveImage(image, "posts", post.getId().toString(), false);
                PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
                postImageRepository.save(postImage);
            }
        }

        post.setTitle(postUpdateRequestDto.getTitle());
        post.setContent(postUpdateRequestDto.getContent());
    }

    @Transactional
    public void deletePost(CustomUserDetails customUserDetails, UUID postId) {

        Member member = customUserDetails.getUser();
        Post post = postRepository.getReferenceById(postId);
        validate(member, post);

        post.setIsDeleted(true);
    }

    @Transactional
    public void deleteImage(CustomUserDetails customUserDetails, UUID imageId) {

        Member member = customUserDetails.getUser();
        PostImage postImage = postImageRepository.getReferenceById(imageId);
        validate(member, postImage.getPost());

        postImageRepository.delete(postImage);
    }

    private void validate(Member member, Post post){
        if (post.getIsDeleted()){
            throw new BizException("삭제된 포스트입니다.", ErrorCode.NOT_FOUND, "");
        } else if (!member.equals(post.getMember())){
            throw new BizException("게시글 생성자와 유저가 불일치합니다.", ErrorCode.INVALID_INPUT, "");
        };
    }
}
