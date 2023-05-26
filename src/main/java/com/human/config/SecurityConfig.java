package com.human.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.human.security.CustomAccessDeniedHandler;
import com.human.security.CustomLoginSuccessHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@EnableWebSecurity 
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	// ########## 의존주입 ############# 
	////////////////////////////////////////////////
	
	@Autowired 	// --> 비밀번호 암호화 객체 의존주입 完
	public PasswordEncoder passwordEncoder;
	
	@Autowired  // --> 데이터 소스 객체 의존주입 完
	private DataSource dataSource;
	
	
	////////////////////////////////////////////////
	
	
	// 오버라이드 1. configure(HttpSecurity http)
	// 설정해야 할 내용들이 아주 많지만 일단 로그인 폼 설정부터 해보자
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// 인가처리를 위한 메소드 ( 권한의 범위 )
		http.authorizeHttpRequests()      //--> 이게 없어서 서버 연결 자체가 불가했음
			.antMatchers("/test").permitAll()
			.antMatchers("/checkpay").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers("/auth/**").permitAll()
			.antMatchers("/board/**").permitAll()
			.antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
			//.antMatchers("/admin/**").hasAnyRole("ADMIN") --> 권한이 한사람에게만 허용되게 하고싶으면 hasAnyRole이 아니라 hasRole() 해야된다. ㅅㅂ그마!!!!!!!!!!!!!!!!!!
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/notice/**").permitAll() // --> notice경로는 여기서는 PermitAll() 이지만 ADMIN만 접근 가능하도록 조치 完
			.antMatchers("/qna/**").permitAll()
			.antMatchers("/checkUserId").permitAll()
			.antMatchers("/mypage").permitAll()
			.antMatchers("/styles/**").permitAll()
			.antMatchers("/images/**").permitAll()
			.antMatchers("/match/**").permitAll()
			.antMatchers("/ground").hasRole("ADMIN")
			.anyRequest().authenticated()			
			;
	
		
		// OAuth2 로그인 기능 활성화
		// http.oauth2Login()
		//	.loginPage("/login");
		
		
		
		
		// 로그인 경로를 시큐리티 기본 경로에서 사용자 지정 경로로 변경
		http.formLogin()
			.loginPage("/auth/login")
			.loginProcessingUrl("/auth/login")
			.successHandler( authenticationSuccessHandler() )
			//.defaultSuccessUrl("/")  --> 이걸 없앴더니 잘돌아가노..
			// 메인경로로 리다이렉트되어서 명령이 넘어가질 않던 현상이었음...
			.permitAll();
	
		// 로그아웃 경로도 설정해준다.
		http.logout()
			.logoutUrl("/auth/logout")  		// 사용자 지정 로그아웃 처리 경로
			.invalidateHttpSession(true)		// 로그아웃 처리 후 세션을 무효화
			.logoutSuccessUrl("/")				// 로그아웃 성공 시, 이동할 페이지 경로
			.permitAll();
		
		// 자동 로그인
		http.rememberMe()
			.key("human")
			.tokenRepository( tokenRepository() )
			.tokenValiditySeconds(60 * 60 * 24 * 7 );
		
		// 접근 거부
		http.exceptionHandling()
			.accessDeniedHandler(new CustomAccessDeniedHandler());
			// 이 클래스가 있으면 접근 권한 없는 곳에 접근 요청 시 메인화면으로 이동한다
	}
	
	
	
	
	
	// 오버라이드 2. configure(AuthenticationManagerBuilder auth)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		// AuthenticationManagerBuilder : 인증 관리 객체
		// 이 메소드에서는 로그인을 시도하는 사용자와 dataSource에 등록되어있는 User 정보가 일치하는지 확인하고
		// 인증 처리 + 인가 처리 진행한다.
		// a : 인증처리 (등록된 사용자인지 판단) -> 아이디 / 비밀번호 / 활성 여부를 조회 
		// b : 인가처리 (자원의 사용 범위 제한) -> 아이디 / 권한을 조회
		// c : 비밀번호 암호화 -> 방식 : BCryptpasswordEncoder
		
		// 아래에서 a,b,c 진행해보자 ↓↓↓↓
		
		// a. 인증 처리를 위한 쿼리
		String sql1 = 	" SELECT user_id as username, user_pw as password, enabled "
				 	  + " FROM users "
				 	  + " WHERE user_id = ? " ;
		// b. 인가 처리를 위한 쿼리
		String sql2 =   " SELECT user_id as username, auth "
				      + " FROM user_auth "
				      + " WHERE user_id =  ? " ;
		
		auth.jdbcAuthentication() 				// JDBC(Java DB Connection) 방식으로 사용자 인증하겠다. 
			.dataSource( dataSource ) 			// 어디에서 정보를 뽑아오나? application.properties에 등록된 dataSource --> 오라클
			.usersByUsernameQuery(sql1) 		// a. 유저 정보 인증 
			.authoritiesByUsernameQuery(sql2)	// b. 유저 권한 인증
			.passwordEncoder(passwordEncoder)	// c. 비밀번호 암호화 방식 
			;
		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring()
	            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}	
	
	// PersistentTokenRepository 객체 생성 메소드
	private PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();
		repositoryImpl.setDataSource(dataSource);
		return repositoryImpl;
		
	}




	// 인증 성공처리 클래스 빈 등록
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomLoginSuccessHandler();
	}


	// 인증 관리자 클래스
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
	
	
}
