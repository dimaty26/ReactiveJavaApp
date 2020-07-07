package zmeevsky.todoreactivedata.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import zmeevsky.todoreactivedata.domain.ToDo;

public interface ToDoRepository extends ReactiveMongoRepository<ToDo, String> {
}
