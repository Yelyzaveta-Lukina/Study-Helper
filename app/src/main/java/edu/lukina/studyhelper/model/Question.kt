package edu.lukina.studyhelper.model

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the ColumnInfo annotation, used to customize column information in a Room database table.
import androidx.room.ColumnInfo
// Imports the Entity annotation, used to mark a class as a Room database table.
import androidx.room.Entity
// Imports the ForeignKey annotation, used to define relationships between database tables.
import androidx.room.ForeignKey
// Imports the PrimaryKey annotation, used to mark a field as the primary key of a database table.
import androidx.room.PrimaryKey

// Declares a 'data class' named Question. Data classes are primarily used for holding data.
// The @Entity annotation marks this class as a table that Room will create in the database.
@Entity(foreignKeys = [
    // The @ForeignKey annotation defines a relationship to another table.
    ForeignKey(entity = Subject::class,
        // Specifies the column(s) in the parent table (Subject) that the foreign key references.
        parentColumns = ["id"],
        // Specifies the column(s) in this table (Question) that hold the foreign key values.
        childColumns = ["subject_id"],
        // Specifies that when a parent Subject is deleted, all its child Questions should also be deleted.
        onDelete = ForeignKey.CASCADE)
])
// Defines the data class 'Question' with its properties.
data class Question (
    // The @PrimaryKey annotation marks the 'id' property as the table's primary key.
    // The 'autoGenerate = true' parameter tells Room
    // to automatically generate a unique value for this key for each new entry.
    @PrimaryKey(autoGenerate = true)
    // Declares a mutable property 'id' of type Long,
    // which will serve as the unique identifier for each question.
    var id: Long = 0,

    // Declares a mutable property 'text' of type String, initialized to an empty string.
    // This will hold the text of the question itself.
    var text: String = "",

    // Declares a mutable property 'answer' of type String, initialized to an empty string.
    // This will hold the answer to the question.
    var answer: String = "",

    // The @ColumnInfo annotation renames the column in the database table to "subject_id".
    @ColumnInfo(name = "subject_id")
    // Declares a mutable property 'subjectId' of type Long,
    // which will be a foreign key linking this question to its corresponding Subject.
    var subjectId: Long = 0
)
