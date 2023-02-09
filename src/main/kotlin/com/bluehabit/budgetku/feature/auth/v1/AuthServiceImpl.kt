package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.data.user.LoginRequest
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.UserResponse
import com.bluehabit.budgetku.data.user.toResponse
import org.springframework.http.HttpStatus.OK
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil
) : AuthService {

    override fun signInWithEmailAndPassword(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(body)

        val encoder = BCryptPasswordEncoder(16)


        val login = userRepository
            .findByUserEmail(
                body.email!!
            ) ?: throw UnAuthorizedException("Username or password didn't match to any account!")

        if (!encoder.matches(
                body.password,
                login.userPassword
            )
        ) throw UnAuthorizedException("Username or password didn't match to any account!")

        val token = jwtUtil.generateToken(login.userEmail)


        return AuthBaseResponse(
            code = OK.value(),
            data = login.toResponse(),
            message = "Sign in success!",
            token = token
        )

    }
}