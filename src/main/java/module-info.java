module ca.ucalgary.groupprojectgui.p3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Persistence (001-postgres-migration)
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires flyway.core;

    opens ca.ucalgary.groupprojectgui.p3 to javafx.fxml;
    exports ca.ucalgary.groupprojectgui.p3;
    exports ca.ucalgary.groupprojectgui.p3.controllers;
    opens ca.ucalgary.groupprojectgui.p3.controllers to javafx.fxml;

    // Needed for MatchmakingLeaderboard.persistence.ModuleSafeResourceProvider to read the
    // Flyway migration SQL via ClassLoader::getResourceAsStream when running on the module path
    // (e.g. `mvn javafx:run`) — that lookup is still subject to package-open rules even for
    // same-module callers.
    opens db.migration;
}