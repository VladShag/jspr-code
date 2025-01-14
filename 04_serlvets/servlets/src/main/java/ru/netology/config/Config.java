package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

@Configuration
public class Config {
    @Bean
    public PostController postController() {
        return new PostController();
    }
    @Bean
    public PostService postService() {
        return new PostService();
    }
    @Bean
    public PostRepository postRepository(){
        return new PostRepository();
    }
}
