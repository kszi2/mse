package hu.kszi2.mse.database

import hu.kszi2.mse.DBPATH
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.io.File
import java.sql.Connection

private var dbInstance: Database? = null //Database instance

/**
 * Connects to the default database and executes the [statement] as a transaction
 * @param statement the transaction
 */
fun <T> dbTransaction(statement: Transaction.() -> T): T {
    // Rely on the initialized dbInstance instead of reconnecting
    return transaction(
        dbInstance?.transactionManager?.defaultIsolationLevel ?: Connection.TRANSACTION_SERIALIZABLE,
        dbInstance?.transactionManager?.defaultReadOnly ?: false,
        dbInstance,
        statement
    )
}

/**
 * Initializes the default database and database connection if the file does not exist
 */
fun dbInitialize() {
    val dbExists = File(DBPATH).exists()
    dbInstance = Database.connect("jdbc:sqlite:$DBPATH", "org.sqlite.JDBC")

    if (dbExists) return

    transaction(dbInstance) {
        SchemaUtils.create(DBOpenings)
        DBOpening.new {
            circleName = "kszi2"
            nextOpeningDate = LocalDateTime(1969, 4, 20, 12, 12, 12)
            outOfStock = false
        }
    }
}