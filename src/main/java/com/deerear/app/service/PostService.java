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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public PostResponseDto getPost(UUID postId) {

        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));
        Member member = post.getMember();
        Boolean isLike = likeRepository.existsByMemberAndTargetTypeAndTargetId(member, Likeable.TargetType.POST, postId);
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        List<String> imageUrls = postImages.stream().map(PostImage::getImageUrl).toList();

        return new PostResponseDto().toResponseDto(post, member, imageUrls, isLike);
    }

    @Transactional(readOnly = true)
    public PostListResponseDto listPosts(PostListRequestDto request){

        List<Post> posts;

        if(request.getKey() == null){
            posts = postRepository.findNextPage(request.getStartLatitude(), request.getStartLongitude(), request.getEndLatitude(), request.getEndLongitude(), Pageable.ofSize(request.getSize()+1));
        } else {
            Post post = postRepository.getReferenceById(UUID.fromString(request.getKey()));
            posts = postRepository.findNextPage(post.getCreatedAt(), post.getId() , request.getStartLatitude(), request.getStartLongitude(), request.getEndLatitude(), request.getEndLongitude(), Pageable.ofSize(request.getSize()+1));
        }

        String nextKey;
        boolean hasNext = false;

        if (posts.size() == 11){
            posts = posts.subList(0,10);
            hasNext = true;
        }

        nextKey = posts.get(posts.size()-1).getId().toString();

        return PostListResponseDto.builder()
                .objects(posts)
                .hasNext(hasNext)
                .nextKey(nextKey)
                .build();
    }

    @Transactional
    public void createPost(CustomUserDetails customUserDetails, PostRequestDto postRequestDto) {

        Post post = postRepository.save(postRequestDto.toEntity(customUserDetails.getUser()));

        List<PostImage> postImages = new ArrayList<>();

        for(MultipartFile image: postRequestDto.getPostImgs()){
            String path = saveImage(image, "posts", post.getId().toString());
            PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
            postImages.add(postImage);
        }

        postImageRepository.saveAll(postImages);
    }

    @Transactional
    public void updatePost(CustomUserDetails customUserDetails, UUID postId, PostRequestDto postRequestDto) {

        Member member = customUserDetails.getUser();

        validate(member, postRequestDto.toEntity(member));

        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));

        List<PostImage> postImgs = postImageRepository.findAllByPostId(postId);

        for(MultipartFile image: postRequestDto.getPostImgs()){
            String path = saveImage(image, "posts", post.getId().toString());
            PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
            postImageRepository.save(postImage);
        }

        return;
    }

    @Transactional
    public void deletePost(CustomUserDetails customUserDetails, UUID postId) {

        Member member = customUserDetails.getUser();
        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));

        validate(member, post);

        post.setIsDeleted(true);
    }

    private void validate(Member member, Post post){
        if (post.getIsDeleted()){
            throw new BizException("삭제된 포스트입니다.", ErrorCode.NOT_FOUND, "");
        } else if (!member.equals(post.getMember())){
            throw new BizException("게시글 생성자와 유저가 불일치합니다.", ErrorCode.INVALID_INPUT, "");
        };
    }
}
