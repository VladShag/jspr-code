package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

// Stub
public class PostRepository {
  private CopyOnWriteArrayList<Post> repo;
  private int idCounter = 1;

  public PostRepository() {
    this.initRepo();
  }
  private CopyOnWriteArrayList<Post> initRepo() {
    if(this.repo != null) return repo;
    else {
      this.repo = new CopyOnWriteArrayList<>();
    }
    return repo;
  }

  public List<Post> all() {
    return repo;
  }

  public Post getById(long id) {
    if(id >= repo.size()) {
      throw new NotFoundException("No post with id " + id + " is exist!");
    } else {
      return repo.get((int) id);
    }
  }

  public Post save(Post post) {
    if(post.getId() == 0) {
      post.setId(idCounter);
      repo.add(post);
      idCounter++;
    } else if (this.getById(post.getId()) != null) {
        Post postToEdit = repo.get((int)post.getId() - 1);
        postToEdit.setContent(post.getContent());
    } else {
      throw new NotFoundException("No post with id " + post.getId() + " is exist! To save new please change id to 0");
    }
    return post;
  }

  public void removeById(long id) {
    if(this.getById(id) != null) {
      repo.remove((int) id);
    } else {
      throw new NotFoundException("No post with id " + id + " is exist!");
    }
  }
}
