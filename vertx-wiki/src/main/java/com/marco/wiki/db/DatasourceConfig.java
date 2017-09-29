package com.marco.wiki.db;

import io.vertx.core.json.JsonObject;

public class DatasourceConfig {
    private final String url;
    private final String user;
    private final String password;

    public DatasourceConfig(JsonObject datasourceConfig) {
        url = datasourceConfig.getString("url", "jdbc:h2:file:../../../var/storage/h2/wikiDb;DB_CLOSE_ON_EXIT=FALSE");
        user = datasourceConfig.getString("user", "sa");
        password = datasourceConfig.getString("password", "");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public JsonObject toJson() {
        return new JsonObject().put("url", url).put("user", user).put("password", password);
    }
}
