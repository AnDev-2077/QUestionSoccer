import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SoccerDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createLeaguesTable = """
            CREATE TABLE leagues (
                id INTEGER PRIMARY KEY,
                name TEXT,
                type TEXT,
                logo TEXT,
                country_name TEXT,
                country_code TEXT,
                country_flag TEXT
            )
        """.trimIndent()

        val createSeasonsTable = """
            CREATE TABLE seasons (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                league_id INTEGER,
                year INTEGER,
                start_date TEXT,
                end_date TEXT,
                current BOOLEAN,
                coverage_fixtures_events BOOLEAN,
                coverage_fixtures_lineups BOOLEAN,
                coverage_fixtures_statistics_fixtures BOOLEAN,
                coverage_fixtures_statistics_players BOOLEAN,
                coverage_standings BOOLEAN,
                coverage_players BOOLEAN,
                coverage_top_scorers BOOLEAN,
                coverage_top_assists BOOLEAN,
                coverage_top_cards BOOLEAN,
                coverage_injuries BOOLEAN,
                coverage_predictions BOOLEAN,
                coverage_odds BOOLEAN,
                FOREIGN KEY (league_id) REFERENCES leagues(id)
            )
        """.trimIndent()

        db.execSQL(createLeaguesTable)
        db.execSQL(createSeasonsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS leagues")
        db.execSQL("DROP TABLE IF EXISTS seasons")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "soccer.db"
        private const val DATABASE_VERSION = 1
    }
}
