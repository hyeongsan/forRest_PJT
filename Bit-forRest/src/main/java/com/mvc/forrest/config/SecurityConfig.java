package com.mvc.forrest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@EnableWebSecurity//해당 파일로 시큐리티를 활성화
@Configuration//IoC
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired private PrincipalOauth2UserService principalOauth2UserService;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
		}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("configure 작동!");
		//super 삭제 - 기존 시큐리티가 가지고 있는 기능이 없어짐 (Overriding)
		http.csrf().disable();//csrf 토큰을 사용하지 않겠다.
		http.authorizeHttpRequests()  //인가 요청이 오면
			.antMatchers("/user/manager","/product/getProduct","/storage/listStorage", "/storage/extendStorage","/old/addOldReport","/old/updateOldState","/old/addOld","/old/updateOld","/old/listOldMine","/old/deleteOld", "/storage/getStorage","http://192.168.0.42:3001/oldChat/**").authenticated()
			.antMatchers("/user/getUser", "/user/deleteUser", "/old/listOldAfterLogin", "/oldLike/addOldLike", "/oldLike/deleteOldLikeOnList","/storage/addStorage", "/produnct/listProductAfterLogin").authenticated()
			.antMatchers("/storage/listStorageForAdmin","/board/addAnnounce","/board/updateAnnounce","/board/deleteAnnounce").authenticated()
			.anyRequest().permitAll()
			.and()
			.formLogin()
			.usernameParameter("userId")
			.passwordParameter("password")
			.loginPage("/user/login") //GET
			.loginProcessingUrl("/user/login") //POST
			.defaultSuccessUrl("/")
			.and()
			.logout()// 로그인과 로그아웃은 csrf 사용시 사용 안할 때는 로그아웃은 get방식도 가능
			.invalidateHttpSession(true)
			.logoutSuccessUrl("/").and()			
			.oauth2Login().loginPage("/user/login")
			.failureUrl("/user/login")
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
			//1. antMatchers 안에 있는 주소에 접속 할 때는 인증이 필요하다
			//2. 그 외 모든 리퀘스트는 다 승인한다
			//3. 그리고
			//4. 인증이 필요한 페이지 요청시 formLogin 할 것이다
			//5. 로그인 요청을 하면 POST 로 온 것만 Intercept
			//6. 로그인 페이지는 /user/login이고, 1의 주소로 인증 없이 접속하면 자동으로 보내준다
			//7. 제대로 로그인 했다면 / 로 redirect.

	}
	
	
}
