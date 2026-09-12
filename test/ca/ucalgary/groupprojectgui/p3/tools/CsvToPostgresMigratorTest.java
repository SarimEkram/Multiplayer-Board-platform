package ca.ucalgary.groupprojectgui.p3.tools;

import Authentication.UserDatabase;
import MatchmakingLeaderboard.PlayerDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvToPostgresMigratorTest {

    @Test
    void migratesValidRowsAndSkipsMalformedOnes(@TempDir Path tempDir) throws IOException {
        Path userDataCsv = tempDir.resolve("userdata.csv");
        Files.writeString(userDataCsv, String.join("\n",
                "userID,username,email,password,winRatio,level,onlineStatus",
                "555001,migrateduser,migrated@example.com,hash123,0.5,3,true",
                "not,a,valid,row" // malformed: wrong column count
        ));

        Path playerDataCsv = tempDir.resolve("playerdata.csv");
        Files.writeString(playerDataCsv, "username,level,userID\n"); // header only, no rows

        Path friendsCsv = tempDir.resolve("friends.csv");
        Files.writeString(friendsCsv, ""); // empty

        CsvToPostgresMigrator.MigrationResult result =
                CsvToPostgresMigrator.migrate(userDataCsv, playerDataCsv, friendsCsv);

        assertEquals(1, result.usersMigrated(), "exactly one valid user row should migrate");

        var migratedUser = UserDatabase.getUserById(555001);
        assertNotNull(migratedUser);
        assertEquals("migrateduser", migratedUser.getUsername());
        assertEquals("migrated@example.com", migratedUser.getEmail());
    }

    @Test
    void reRunningMigrationIsIdempotent(@TempDir Path tempDir) throws IOException {
        Path userDataCsv = tempDir.resolve("userdata.csv");
        Files.writeString(userDataCsv, String.join("\n",
                "userID,username,email,password,winRatio,level,onlineStatus",
                "555002,repeatuser,repeat@example.com,hash456,0.7,5,false"
        ));
        Path playerDataCsv = tempDir.resolve("playerdata.csv");
        Files.writeString(playerDataCsv, "username,level,userID\n");
        Path friendsCsv = tempDir.resolve("friends.csv");
        Files.writeString(friendsCsv, "");

        CsvToPostgresMigrator.migrate(userDataCsv, playerDataCsv, friendsCsv);
        CsvToPostgresMigrator.migrate(userDataCsv, playerDataCsv, friendsCsv);

        var migratedUser = UserDatabase.getUserById(555002);
        assertNotNull(migratedUser);
        assertEquals("repeatuser", migratedUser.getUsername(), "second run should update, not duplicate, the row");
    }

    @Test
    void skipsOrphanedFriendshipReferences(@TempDir Path tempDir) throws IOException {
        Path userDataCsv = tempDir.resolve("userdata.csv");
        Files.writeString(userDataCsv, "userID,username,email,password,winRatio,level,onlineStatus\n");
        Path playerDataCsv = tempDir.resolve("playerdata.csv");
        Files.writeString(playerDataCsv, "username,level,userID\n");
        Path friendsCsv = tempDir.resolve("friends.csv");
        // Neither 999901 nor 999902 exist in users — should be skipped, not inserted.
        Files.writeString(friendsCsv, "999901:999902\n");

        CsvToPostgresMigrator.MigrationResult result =
                CsvToPostgresMigrator.migrate(userDataCsv, playerDataCsv, friendsCsv);

        assertEquals(0, result.friendshipsMigrated(), "orphaned friendship reference should be skipped");
    }

    @Test
    void missingCsvFilesResultInZeroMigratedRows(@TempDir Path tempDir) {
        CsvToPostgresMigrator.MigrationResult result = CsvToPostgresMigrator.migrate(
                tempDir.resolve("does-not-exist-users.csv"),
                tempDir.resolve("does-not-exist-players.csv"),
                tempDir.resolve("does-not-exist-friends.csv"));

        assertEquals(0, result.usersMigrated());
        assertEquals(0, result.playersMigrated());
        assertEquals(0, result.friendshipsMigrated());
    }
}
