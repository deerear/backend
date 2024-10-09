package com.deerear.app.service;

import com.deerear.app.domain.Like;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.domain.PostImage;
import com.deerear.app.dto.*;
import com.deerear.app.repository.LikeRepository;
import com.deerear.app.repository.MemberRepository;
import com.deerear.app.repository.PostImageRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public PostResponseDto getPost(UUID postId) {

        Post post = validate(postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, "")));
        Member member = post.getMember();
        Boolean isLike = likeRepository.existsByMemberAndPost(member, post);
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        List<String> imageUrls = postImages.stream().map(PostImage::getImageUrl).toList();

        return new PostResponseDto().toResponseDto(post, member, imageUrls, isLike);
    }

//    @Transactional(readOnly = true)
//    public List<MemberGetPostsResponseDto> getPosts(Member member, PagingRequestDto pagingRequestDto) {
//        PageRequest pageRequest = PageRequest.of(0, pagingRequestDto.getSize());
//        Page<Post> postPage = postRepository.findPostsByMember(member, pageRequest);
//
//        boolean hasNext = postPage.hasNext();  // 다음 페이지 여부 확인
//        return postPage.getContent().stream()
//                .map(post -> MemberGetPostsResponseDto.toDto(
//                        post.getId(),
//                        post.getTitle(),
//                        post.getContent(),
//                        post.getCreatedAt(),
//                        pagingRequestDto.getSize(),
//                        pagingRequestDto.getKey(),
//                        hasNext
//                ))
//                .collect(Collectors.toList());
//    }
//
    @Transactional(readOnly = true)
    public ListDto listPosts(String nextKey, Integer size, BigDecimal startLatitude, BigDecimal startLongitude, BigDecimal endLatitude, BigDecimal endLongitude){

        return new ListDto();
    }

    @Transactional
    public void createPost(String auth, PostRequestDto postRequestDto) {

        validate(postRequestDto);

        String email = jwtTokenProvider.getUsernameFromToken(auth.substring(7));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BizException("존재하지 않는 유저 입니다.", ErrorCode.NOT_FOUND, ""));
        Post post = postRepository.save(postRequestDto.toEntity(member));

        List<PostImage> postImages = new ArrayList<>();

        for(MultipartFile image: postRequestDto.getPostImgs()){
            String path = saveImage(image, "posts", post.getId().toString());
            PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
            postImages.add(postImage);
        }

        postImageRepository.saveAll(postImages);
    }

    @Transactional
    public void updatePost(String auth, UUID postId, PostRequestDto postRequestDto) {

        validate(postRequestDto);

        String email = jwtTokenProvider.getUsernameFromToken(auth.substring(7));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BizException("존재하지 않는 유저 입니다.", ErrorCode.NOT_FOUND, ""));
        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));

        for(MultipartFile image: postRequestDto.getPostImgs()){
            String path = saveImage(image, "posts", post.getId().toString());
            PostImage postImage = PostImage.builder().post(post).imageUrl(path).build();
            postImageRepository.save(postImage);
        }

        return;
    }

    @Transactional
    public void deletePost(String auth, UUID postId) {

        String email = jwtTokenProvider.getUsernameFromToken(auth.substring(7));
        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BizException("존재하지 않는 유저 입니다.", ErrorCode.NOT_FOUND, ""));

        validate(member, post);

        post.setIsDeleted(true);
    }

    private void validate(PostRequestDto request) {
        if (request.getTitle() == null){
            throw new BizException("제목은 필수 입력 값입니다.", ErrorCode.INVALID_INPUT, "");
        } else if (request.getContent() == null) {
            throw new BizException("내용은 필수 입력 값입니다.", ErrorCode.INVALID_INPUT, "");
        }
    }

    private Post validate(Post post){
        if (post.getIsDeleted()){
            throw new BizException("삭제된 포스트입니다.", ErrorCode.NOT_FOUND, "");
        }
        return post;
    }

    private Post validate(Member member, Post post){
        if (post.getIsDeleted()){
            throw new BizException("삭제된 포스트입니다.", ErrorCode.NOT_FOUND, "");
        } else if (!member.equals(post.getMember())){
            throw new BizException("게시글 생성자와 유저가 불일치합니다.", ErrorCode.INVALID_INPUT, "");
        };
        return post;
    }
}
