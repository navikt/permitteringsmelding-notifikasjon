package no.nav.permitteringsvarsel.notifikasjon.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import kotliquery.*
import java.util.*

class Database {
    private val dataSource: HikariDataSource = hikari()

    init {
        val flyway = Flyway.configure()
            .locations("db.migration")
            .dataSource(dataSource)
            .load()
        flyway.migrate()
    }

    companion object {
        private fun hikari(): HikariDataSource {
            val config = HikariConfig()
            config.driverClassName = "org.h2.Driver"
            config.jdbcUrl = "jdbc:h2:mem:test"
            config.maximumPoolSize = 3
            config.isAutoCommit = false
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            config.validate()
            return HikariDataSource(config)
        }
    }


    fun runQuery() {
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf("""
                select 1 from melding
            """.trimIndent()).asExecute)
        }
    }

    fun hentMeldingerForNotifikasjon() {
        val tilMelding: (Row)-> Melding = { row ->
            Melding(
                UUID.fromString(row.stringOrNull("id")),
                row.string("opprettet_av"),
                row.string("bedrifts_nr"),
                row.localDate("varslet_nav_dato"),
                row.localDate("start_dato"),
                row.localDateOrNull("slutt_dato")
            )
        }
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf("""
                select * from melding where (now() + interval '2 week') > start_dato;
            """.trimIndent()).map(tilMelding))
        }
    }

}