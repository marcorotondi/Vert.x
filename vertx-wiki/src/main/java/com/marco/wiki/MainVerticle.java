package com.marco.wiki;

import com.marco.wiki.db.DatasourceConfig;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import org.flywaydb.core.Flyway;
import rx.Single;

public class MainVerticle extends AbstractVerticle {
    private DatasourceConfig datasourceConfig;
    private JDBCClient dbClient;

    @Override
    public void start(Future<Void> startFeature) throws Exception {
        datasourceConfig = new DatasourceConfig(config().getJsonObject("dataSource", new JsonObject()));
        prepareDataBase()
                .flatMap(v -> prepareWebServer())
                .subscribe(startFeature::complete, startFeature::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        vertx.<Void>rxExecuteBlocking(io.vertx.rxjava.core.Future::complete).onErrorReturn(throwable -> {
            throwable.printStackTrace();
            return null;
        }).subscribe(stopFuture::complete, stopFuture::fail);
    }


    private Single<Void> prepareDataBase() {
        return vertx.rxExecuteBlocking(future -> {
            final Flyway flyway = new Flyway();
            flyway.setDataSource(datasourceConfig.getUrl(), datasourceConfig.getUser(), datasourceConfig.getPassword());
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
