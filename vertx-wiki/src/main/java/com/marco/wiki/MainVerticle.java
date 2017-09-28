package com.marco.wiki;

import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import org.flywaydb.core.Flyway;
import rx.Single;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFeature) throws Exception {
        prepareDataBase()
                .flatMap(v -> prepareWebServer())
                .subscribe(startFeature::complete, startFeature::fail);
    }


    private Single<Void> prepareDataBase() {
        return vertx.rxExecuteBlocking(future -> {
            final Flyway flyway = new Flyway();
            flyway.setDataSource(null, null, null);
            flyway.migrate();
            future.complete();
        });
    }

    private Single<Void> prepareWebServer() {
       return vertx.createHttpServer()
                //.requestHandler(router::accept)
                .rxListen(8080)
                .map(server -> null);
    }

}
