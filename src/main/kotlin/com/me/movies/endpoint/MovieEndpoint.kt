package com.me.movies.endpoint

import com.me.movies.exception.NotFoundException
import com.me.movies.model.Movie
import com.me.movies.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

@RestController
@RequestMapping(value = ["/movie"])
class MovieEndpoint @Autowired constructor(private val repository: MovieRepository) {

    @GetMapping(produces = ["application/stream+json"])
    fun get(): Flux<Movie> {
        return repository.findAll()
                .delayElements(Duration.ofSeconds(3))
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun post(@RequestBody movie: Movie): Mono<Movie> {
        movie.id = UUID.randomUUID().toString()

        return repository.save(movie)
    }

    @PutMapping
    fun put(@RequestBody movie: Movie): Mono<Movie> {
        return repository.save(movie)
    }

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Void> {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException))
                .flatMap { movie -> repository.delete(movie) }
                .then(Mono.empty())
    }

}