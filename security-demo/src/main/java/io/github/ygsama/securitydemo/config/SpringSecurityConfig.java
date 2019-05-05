package io.github.ygsama.securitydemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * spring security 配置文件
 * 注意观察 INFO 级别日志 ： 以" Creating filter chain: ...."
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DaoAuthenticationProvider provider;

    /**
     * 启动时，创建多个过滤器链，静态文件忽略拦截
     * Creating filter chain: Ant [pattern='/js/**'], []
     * Creating filter chain: Ant [pattern='/css/**'], []
     * Creating filter chain: Ant [pattern='/images/**'], []
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    /**
     * 其他所有的请求走这过滤器链
     * Creating filter chain:
     *  any request,[
     *      WebAsyncManagerIntegrationFilter,
     *      SecurityContextPersistenceFilter,
     *      HeaderWriterFilter,
     *      LogoutFilter,
     *      UsernamePasswordAuthenticationFilter,
     *      DefaultLoginPageGeneratingFilter,
     *      DefaultLogoutPageGeneratingFilter,
     *      RequestCacheAwareFilter,
     *      SecurityContextHolderAwareRequestFilter,
     *      AnonymousAuthenticationFilter,
     *      SessionManagementFilter,
     *      ExceptionTranslationFilter,
     *      FilterSecurityInterceptor
     * ]
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()//访问首页不需要权限
                .anyRequest().authenticated() // 其他页面需要权限
                .and().logout().permitAll() //退出不需要权限
                .and().formLogin()          //支持表单登陆
                .and().csrf().disable();    //关闭默认的csrf认证
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);  // 自定义provider
        auth.eraseCredentials(false);           // 不删除凭据，以便记住用户
    }


    /**
     * 手动在拦截器中配置注册一个单例的bean对象，避免每次都重新生成
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
