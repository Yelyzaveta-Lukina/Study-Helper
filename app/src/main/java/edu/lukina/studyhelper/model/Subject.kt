package edu.lukina.studyhelper.model

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the NonNull annotation, used to indicate that a variable or parameter cannot be null.
import androidx.annotation.NonNull
// Imports the ColumnInfo annotation, used to customize column information in a Room database table.
import androidx.room.ColumnInfo
// Imports the Entity annotation, used to mark a class as a Room database table.
import androidx.room.Entity
// Imports the PrimaryKey annotation, used to mark a field as the primary key of a database table.
import androidx.room.PrimaryKey


// Declares a 'data class' named Subject, which is a special class in Kotlin for holding data.
// The @Entity annotation marks this class as a table that Room will create in the database.
@Entity
// Defines the data class 'Subject' with its properties.
data class Subject (
    // The @PrimaryKey annotation marks the 'id' property as the table's primary key.
    // The 'autoGenerate = true' parameter tells Room
    // to automatically generate a unique value for this key for each new entry.
    @PrimaryKey(autoGenerate = true)
    // Declares a mutable property 'id' of type Long,
    // which will serve as the unique identifier for each subject.
    var id: Long = 0,
    // The @NonNull annotation indicates that the 'text' property must not be null.
    @NonNull
    // Declares a mutable property 'text' of type String, which will hold the name of the subject.
    var text: String,
    // The @ColumnInfo annotation renames the column in the database table to "updated".
    @ColumnInfo(name = "updated")
    // Declares a mutable property 'updateTime' of type Long,
    // initialized with the current system time.
    // This serves as a timestamp for when the subject was last updated.
    var updateTime: Long = System.currentTimeMillis()
)
