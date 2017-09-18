package com.marco.third;

import com.marco.third.model.PostBean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyThirdVerticle extends AbstractVerticle {
    private final Map<Integer, PostBean> posts = new LinkedHashMap<>();

    @Override
    public void start(Future<Void> fut) throws Exception {
        final Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<H1>Hello By VERT.X Intre' Camp</H1>");
        });

        router.route("/asset/*").handler(StaticHandler.create("asset"));

        router.route("/api/posts").handler(this::getAll);

        router.route("/api/posts*").handler(BodyHandler.create());

        router.post("/api/posts").handler(this::addPost);

        router.delete("/api/posts/:id").handler(this::deletePost);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
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

    private void deletePost(final RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            posts.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void addPost(final RoutingContext routingContext) {
        final PostBean post = Json.decodeValue(routingContext.getBodyAsString(), PostBean.class);

        posts.put(post.getId(), post);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(post));
    }

    private void getAll(final RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(posts));
    }
}
