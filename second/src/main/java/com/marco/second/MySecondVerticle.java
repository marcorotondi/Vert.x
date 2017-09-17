package com.marco.second;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MySecondVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx.createHttpServer()
                .requestHandler(r -> {
                    r.response().end("<H1>Hello By VERT.X Intre' Camp</H1>");
                }).listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 9999),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }
}
