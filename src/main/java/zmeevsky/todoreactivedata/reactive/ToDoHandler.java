package zmeevsky.todoreactivedata.reactive;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import zmeevsky.todoreactivedata.domain.ToDo;
import zmeevsky.todoreactivedata.repository.ToDoRepository;

import static org.springframework.web.reactive.function.BodyInserters.
        fromObject;
import static org.springframework.web.reactive.function.BodyInserters.
        fromPublisher;

@Component
public class ToDoHandler {

    private ToDoRepository toDoRepository;

    public ToDoHandler(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public Mono<ServerResponse> getToDo(ServerRequest request) {
        return findById(request.pathVariable("id"));
    }

    public Mono<ServerResponse> getToDos(ServerRequest request) {
        Flux<ToDo> toDos = this.toDoRepository.findAll();
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(toDos, ToDo.class);
    }

    public Mono<ServerResponse> newToDo(ServerRequest request) {
        Mono<ToDo> toDo = request.bodyToMono(ToDo.class);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(toDo.flatMap(this::save), ToDo.class));
    }

    private Mono<ServerResponse> findById(String id) {
        Mono<ToDo> toDoMono = this.toDoRepository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return toDoMono
                .flatMap(t -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(t)))
                .switchIfEmpty(notFound);
    }

    private Mono<ToDo> save(ToDo toDo) {
        return Mono.fromSupplier(
                () -> {
                    toDoRepository
                            .save(toDo)
                            .subscribe();
                    return toDo;
                }
        );
    }
}
