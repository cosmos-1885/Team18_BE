package com.example.demo.user;

import com.example.demo.config.auth.CustomUserDetails;
import com.example.demo.config.jwt.JWTTokenProvider;
import com.example.demo.config.utils.ApiResponseBuilder;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

@Api(tags = "User API")
@RequiredArgsConstructor
@RestController
public class UserRestController {

    private final UserService userService;

    @Operation(summary = "이메일 중복 확인", description = "동일한 이메일이 존재하는지 확인한다.")
    @PostMapping(value = "/users/emailcheck")
    public ResponseEntity<?> emailCheck(@RequestBody @Valid UserRequest.EmailCheckDTO requestDTO, Errors errors) {
        userService.emailCheck(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.successWithNoContent());
    }

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping(value = "/users/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest.SignUpDTO requestDTO, Errors errors) {
        userService.signup(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.successWithNoContent());
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping(value = "/users/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO requestDTO, Errors errors, HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        UserResponse.LoginDTO responseDTO = userService.login(requestDTO);

        String accessToken = JWTTokenProvider.Token_Prefix + responseDTO.getJwtToken().getAccessToken();
        String refreshToken = JWTTokenProvider.Token_Prefix + responseDTO.getJwtToken().getRefreshToken();
        refreshToken = URLEncoder.encode(refreshToken, "utf-8");

        httpServletResponse.addHeader(JWTTokenProvider.Header, accessToken);
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 5);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(responseDTO.getUserDetailDTO()));
    }

    @Operation(summary = "마이 페이지 프로필 확인", description = "마이 페이지에서 프로필 확인")
    @GetMapping(value =  {"/profiles", "/profiles/{id}"})
    public ResponseEntity<?> profile(@PathVariable(required = false) Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse.ProfileDTO responseDTO = userService.findProfile(id, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(responseDTO));
    }
    
    @Operation(summary = "마이 페이지 프로필 수정", description = "마이 페이지에서 프로필 수정")
    @PutMapping(value = "/profiles")
    public ResponseEntity<?> profileUpdate(@RequestBody @Valid UserRequest.ProfileUpdateDTO requestDTO, Errors errors, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse.ProfileDTO responseDTO = userService.updateProfile(userDetails.getUser().getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(responseDTO));
    }
}
