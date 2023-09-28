package hu.kszi2.mse.database

import hu.kszi2.mse.DBPATH
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.io.File


fun <T> dbTransaction(statement: Transaction.() -> T): T {
    val db: Database? = null
    Database.connect("jdbc:sqlite:$DBPATH", "org.sqlite.JDBC")
    return transaction(
        db.transactionManager.defaultIsolationLevel,
        db.transactionManager.defaultReadOnly,
        db,
        statement
    )
}

fun dbInitialize() {
    if (File(DBPATH).exists())
        return

    Database.connect("jdbc:sqlite:$DBPATH", "org.sqlite.JDBC")
    transaction {
        addLogger(Slf4jSqlDebugLogger)

        SchemaUtils.create(DBOpenings)

        DBOpening.new {
            circleName = "kszi2"
            nextOpeningDate = LocalDateTime(1969, 4, 20, 12, 12, 12)
            outOfStock = false
        }
    }
}