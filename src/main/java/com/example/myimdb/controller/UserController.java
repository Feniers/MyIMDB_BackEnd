package com.example.myimdb.controller;

import com.example.myimdb.authorization.annotation.LoginRequire;
import com.example.myimdb.authorization.annotation.CurrentUser;
import com.example.myimdb.authorization.manager.TokenManager;
import com.example.myimdb.authorization.model.TokenModel;
import com.example.myimdb.domain.ResultStatus;
import com.example.myimdb.domain.Result;
import com.example.myimdb.domain.User;
import com.example.myimdb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "用户相关接口")
@RestController
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenManager tokenManager;

    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/user")
    public ResponseEntity<Result> register(@RequestBody User user) {
        Assert.hasLength(user.getUsername(), "用户名不能为空");
        Assert.hasLength(user.getPassword(), "密码不能为空");

        User u = userService.findByUsername(user.getUsername());
        if (u != null) {
            //提示用户名已存在
            return new ResponseEntity<>(Result.error(ResultStatus.USERNAME_EXIST), HttpStatus.BAD_REQUEST);
        }
        userService.getBaseMapper().insert(user);
        return new ResponseEntity<>(Result.ok(), HttpStatus.CREATED);
    }

    @Operation(summary = "用户登录", description = "用户登录接口")
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "password", description = "密码", required = true)
    })
    @PostMapping("/user/login")
    public ResponseEntity<Result> login(@RequestParam @NotEmpty(message = "用户名不能为空") String username,
                                        @RequestParam @NotEmpty(message = "密码不能为空") String password) {

        User user = userService.findByUsername(username);
        if (user == null ||  //未注册
                !user.getPassword().equals(password)) {  //密码错误
            //提示用户名或密码错误
            return new ResponseEntity<>(Result.error(ResultStatus.USERNAME_OR_PASSWORD_ERROR), HttpStatus.BAD_REQUEST);
        }
        //生成一个token，保存用户登录状态
        TokenModel model = tokenManager.createToken(user.getId());
        return new ResponseEntity<>(Result.ok(model), HttpStatus.OK);
    }

    @Operation(summary = "用户登出", description = "用户登出接口")
    @Parameter(name = "Authorization", description = "token", required = true, in = ParameterIn.HEADER)
    @DeleteMapping("/user")
    @LoginRequire
    public ResponseEntity<Result> logout(@CurrentUser User user) {
        tokenManager.deleteToken(user.getId());
        return new ResponseEntity<>(Result.ok(), HttpStatus.OK);
    }

    @Operation(summary = "获取用户信息", description = "获取用户信息接口")
    @Parameter(name = "Authorization", description = "token", required = true, in = ParameterIn.HEADER)
    @LoginRequire
    @GetMapping("/user/me")
    public ResponseEntity<Result> getUser(@CurrentUser User user) {
        return new ResponseEntity<>(Result.ok(user), HttpStatus.OK);
    }

    @Operation(summary = "更新用户信息", description = "更新用户信息接口")
    @Parameter(name = "Authorization", description = "token", required = true, in = ParameterIn.HEADER)
    @PutMapping("/user")
    @LoginRequire
    public ResponseEntity<Result> updateUser(@CurrentUser User user, @RequestBody User newUser) {
        if (userService.getById(user.getId()) == null) {
            return new ResponseEntity<>(Result.error(ResultStatus.RESOURCE_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        if(newUser.getUsername() != null) {
            user.setUsername(newUser.getUsername());
        }
        if(newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
        if(newUser.getNickname() != null) {
            user.setNickname(newUser.getNickname());
        }
        if(newUser.getRole() != null) {
            user.setRole(newUser.getRole());
        }
        if(userService.getBaseMapper().updateById(user) > 0) {
            return new ResponseEntity<>(Result.ok(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Result.error(501,"更新失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
