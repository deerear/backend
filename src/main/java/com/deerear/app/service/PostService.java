package com.deerear.app.service;

import com.deerear.app.domain.*;
import com.deerear.app.dto.*;
import com.deerear.app.repository.LikeRepository;
import com.deerear.app.repository.MemberRepository;
import com.deerear.app.repository.PostImageRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.deerear.app.util.StaticFiles.saveImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public PostDetailResponseDto getPost(UUID postId) {

        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));
        Member member = post.getMember();
        Boolean isLike = likeRepository.existsByMemberAndTargetTypeAndTargetId(member, Likeable.TargetType.POST, postId);
        List<PostImage> postImageList = postImageRepository.findAllByPostId(postId);
        List<PostImageDto> postImageListDto = postImageList.stream().map(postImage -> PostImageDto.builder().id(postImage.getId()).url(postImage.getImageUrl()).build()).toList();

        return post.toDto(member, postImageListDto, isLike);
    }

    @Transactional(readOnly = true)
    public PagingResponseDto getPosts(Member member, PagingRequestDto pagingRequestDto) {
        String lastPostId = pagingRequestDto.getKey(); // 커서로 사용될 key
        //int size = pagingRequestDto.getSize();

        PageRequest pageRequest = PageRequest.of(0, 10); // 페이징 처리

        List<Post> posts;
        if (lastPostId == null) {
            // 첫 페이지를 요청하는 경우
            posts = postRepository.findFirstByMember(member, pageRequest);
        } else {
            // 커서를 기준으로 그 이후의 게시물을 가져옴
            posts = postRepository.findPostsByMemberAndIdGreaterThan(member, UUID.fromString(lastPostId), pageRequest);
        }

        boolean hasNext = posts.size() == 10; // 다음 페이지 여부 확인

        List<Object> postDtos = posts.stream()
                .map(post -> MemberGetPostsResponseDto.toDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 게시글 리스트와 페이징 정보를 담은 PagedResponseDto 반환
        return new PagingResponseDto(postDtos, 10, lastPostId, hasNext);
    }

    @Transactional(readOnly = true)
    public PostListResponseDto listPosts(CustomUserDetails customUserDetails, PostListRequestDto postListRequestDto, Optional<String> nextKey, Integer size){

        Member member = customUserDetails.getUser();

        List<Post> posts;

        if(nextKey.isEmpty()){
            posts = postRepository.findNextPage(postListRequestDto.getStartLatitude(), postListRequestDto.getStartLongitude(), postListRequestDto.getEndLatitude(), postListRequestDto.getEndLongitude(), Pageable.ofSize(size+1));
        } else {
            Post post = postRepository.getReferenceById(UUID.fromString(nextKey.orElseThrow()));
            posts = postRepository.findNextPage(post.getCreatedAt(), post.getId() , postListRequestDto.getStartLatitude(), postListRequestDto.getStartLongitude(), postListRequestDto.getEndLatitude(), postListRequestDto.getEndLongitude(), Pageable.ofSize(size+1));
        }

        String key = "";
        boolean hasNext = false;

        if (posts.size() == 11){
            posts = posts.subList(0,10);
            hasNext = true;
            key = posts.get(posts.size()-1).getId().toString();
        }

        List<PostDto> postListDto = new ArrayList<>();
        for(Post post : posts){
            Boolean isLike = likeRepository.existsByMemberAndTargetTypeAndTargetId(member, Likeable.TargetType.POST, post.getId());
            postListDto.add(post.toDto(isLike));
        }


        return PostListResponseDto.builder()
                .objects(postListDto)
                .hasNext(hasNext)
                .nextKey(key)
                .size(postListDto.size())
                .build();
    }

    @Transactional
    public void createPost(CustomUserDetails customUserDetails, PostCreateRequestDto postCreateRequestDto) {

        Post post = postRepository.save(postCreateRequestDto.toEntity(customUserDetails.getUser()));

        if (postCreateRequestDto.getPostImgs() != null) {
            List<PostImage> postImages = new ArrayList<>();
            for(MultipartFile image: postCreateRequestDto.getPostImgs()){
                String path = saveImage(image, "posts", post.getId().toString());
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

        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));

        if (postUpdateRequestDto.getPostImgs() != null){
            for(MultipartFile image: postUpdateRequestDto.getPostImgs()){
                String path = saveImage(image, "posts", post.getId().toString());
                PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
                postImageRepository.save(postImage);
            }
        }

        post.setTitle(postUpdateRequestDto.getTitle());
        post.setContent(postUpdateRequestDto.getContent());

        return ;
    }

    @Transactional
    public void deletePost(CustomUserDetails customUserDetails, UUID postId) {

        Member member = customUserDetails.getUser();
        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));
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
