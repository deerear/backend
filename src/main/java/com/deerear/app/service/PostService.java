package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.domain.PostImage;
import com.deerear.app.dto.PostRequestDto;
import com.deerear.app.dto.PostResponseDto;
import com.deerear.app.repository.MemberRepository;
import com.deerear.app.repository.PostImageRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.app.util.StaticFiles;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.deerear.app.util.StaticFiles.saveImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public PostResponseDto getPost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new BizException("존재하지 않는 포스트 입니다.", ErrorCode.NOT_FOUND, ""));
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);

        return PostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .postImgs(postImages)
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .commentCount(post.getCommentCount())
                .likesCount(post.getLikeCount())
                .build();
    }

    @Transactional(readOnly = true)
    public void listPosts(){
        return;
    }

    @Transactional
    public PostResponseDto createPost(String auth, PostRequestDto postRequestDto) throws IOException {

        validation(postRequestDto);

        System.out.println("AUTH : " + auth);
        String email = jwtTokenProvider.getUsernameFromToken(auth.substring(7));

        System.out.println("EMAIL : " + email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BizException("존재하지 않는 유저 입니다.", ErrorCode.NOT_FOUND, ""));

        Post post = postRepository.save(
                Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .member(member)
                .latitude(postRequestDto.getLatitude())
                .longitude(postRequestDto.getLongitude())
                .build()
        );

        for(MultipartFile image: postRequestDto.getPostImgs()){
            String path = saveImage(image, "posts", post.getId().toString());

            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(path)
                    .build();

            postImageRepository.save(postImage);
        }
        return new PostResponseDto();
    }

    @Transactional
    public void updatePost(PostRequestDto postRequestDto) {

        Post request = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .latitude(postRequestDto.getLatitude())
                .longitude(postRequestDto.getLongitude())
                .build();

        postRepository.save(request);

        return;
    }

    @Transactional
    public void deletePost(Post post) {
        postRepository.delete(post);
        return;
    }

    private void validation(PostRequestDto request) {
        if (request.getTitle() == null){
            throw new BizException("제목은 필수 입력 값입니다.", ErrorCode.INVALID_INPUT, "");
        } else if (request.getContent() == null) {
            throw new BizException("내용은 필수 입력 값입니다.", ErrorCode.INVALID_INPUT, "");
        }
    }
}
