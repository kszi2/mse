package hu.kszi2.mse.database

import hu.kszi2.mse.DBPATH
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.io.File


fun <T> dbTransaction(db: Database? = null, statement: Transaction.() -> T): T {
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
            nextOpeningDate = LocalDate.fromEpochDays(36000).toEpochDays().toLong()
            outOfStock = false
        }

    }
}