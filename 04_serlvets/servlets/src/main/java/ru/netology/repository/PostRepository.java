package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {
  private ConcurrentHashMap<Long, Post> repo;
  private AtomicLong idCounter;

  public PostRepository() {
    idCounter = new AtomicLong();
    idCounter.set(1);
    this.initRepo();
  }
  private ConcurrentHashMap<Long, Post> initRepo() {
    if(this.repo != null) return repo;
    else {
      this.repo = new ConcurrentHashMap<>();
    }
    return repo;
  }

  public List<Post> all() {
    return repo.values().stream().toList();
  }

  public Post getById(long id) {
    if(idCounter.get() < id) {
      throw new NotFoundException("No post with id " + id + " is exist!");
    } else {
      return repo.get(id);
    }
  }

  public Post save(Post post) {
    if(post.getId() == 0) {
      Long ident = idCounter.getAndIncrement();
      post.setId(ident);
      repo.put(ident, post);
    } else if (this.getById(post.getId()) != null) {
        Post postToEdit = repo.get(post.getId());
        postToEdit.setContent(post.getContent());
    } else {
      throw new NotFoundException("No post with id " + post.getId() + " is exist! To save new please change id to 0");
    }
    return post;
  }

  public void removeById(long id) {
    if(this.getById(id) != null) {
      repo.remove(id);
    } else {
      throw new NotFoundException("No post with id " + id + " is exist!");
    }
  }
}
