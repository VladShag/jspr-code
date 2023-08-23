package ru.netology.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

  private PostController controller;
  private static final String METHOD_GET = "GET";
  private static final String METHOD_POST = "POST";
  private static final String METHOD_DELETE = "DELETE";
  private static final String POSTS_PATH = "/api/posts";
  private static final String POST_ID_PATH = "/api/posts/\\d+";
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationConfig.xml");

  @Override
  public void init() {
    controller = context.getBean("postController", PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(METHOD_GET) && path.equals(POSTS_PATH)) {
        controller.all(resp);
        return;
      }
      if (method.equals(METHOD_GET) && path.matches(POST_ID_PATH)) {
        // easy way
        final var id = getIdFromPath(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(METHOD_POST) && path.equals(POSTS_PATH)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(METHOD_DELETE) && path.matches(POST_ID_PATH)) {
        // easy way
        final var id = getIdFromPath(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  private static long getIdFromPath(String path) {
    long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    return id;
  }
}

