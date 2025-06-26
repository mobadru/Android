package com.example.semproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserManagement.db";
    public static final int DATABASE_VERSION = 1;

    // Role constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TENANT = "TENANT";

    // USERS TABLE
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";

    // ROOMS TABLE
    public static final String TABLE_ROOMS = "rooms";
    public static final String COL_ROOM_ID = "id";
    public static final String COL_ROOM_NUMBER = "room_number";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_TYPE = "type";
    public static final String COL_RENT_AMOUNT = "rent_amount";
    public static final String COL_STATUS = "status";

    // LEASE AGREEMENT TABLE
    public static final String TABLE_LEASE = "leaseAgreement";
    public static final String COL_LEASE_ID = "id";
    public static final String COL_LEASE_USER_ID = "user_id";
    public static final String COL_LEASE_ROOM_ID = "room_id";
    public static final String COL_LEASE_START = "lease_start";
    public static final String COL_LEASE_END = "lease_end";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_ROLE + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        String createRoomsTable = "CREATE TABLE " + TABLE_ROOMS + " (" +
                COL_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ROOM_NUMBER + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_TYPE + " TEXT NOT NULL, " +
                COL_RENT_AMOUNT + " REAL NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL)";
        db.execSQL(createRoomsTable);

        String createLeaseTable = "CREATE TABLE " + TABLE_LEASE + " (" +
                COL_LEASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LEASE_USER_ID + " INTEGER NOT NULL, " +
                COL_LEASE_ROOM_ID + " INTEGER NOT NULL, " +
                COL_LEASE_START + " TEXT, " +
                COL_LEASE_END + " TEXT, " +
                "FOREIGN KEY(" + COL_LEASE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_LEASE_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COL_ROOM_ID + "))";
        db.execSQL(createLeaseTable);

        // Insert default admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_NAME, "Admin");
        adminValues.put(COL_EMAIL, "admin@gmail.com");
        adminValues.put(COL_PASSWORD, "123");
        adminValues.put(COL_ROLE, ROLE_ADMIN);
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEASE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ------------------ USER CRUD ------------------

    public boolean insertUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_ROLE, role == null || role.trim().isEmpty() ? ROLE_TENANT : role);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean insertUserAutoRole(String name, String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        String role = ROLE_TENANT;
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) == 0) {
                role = ROLE_ADMIN;
            }
        }
        cursor.close();
        return insertUser(name, email, password, role);
    }

    public User checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
            user = new User(id, name, email, password, role);
        }
        cursor.close();
        db.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
                userList.add(new User(id, name, email, password, role));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public boolean updateUser(int id, String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_ROLE, role);
        int rows = db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USERS, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // ------------------ ROOM CRUD ------------------

    public boolean insertRoom(String roomNumber, String description, String type, double rentAmount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROOM_NUMBER, roomNumber);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_TYPE, type);
        values.put(COL_RENT_AMOUNT, rentAmount);
        values.put(COL_STATUS, status);
        long result = db.insert(TABLE_ROOMS, null, values);
        return result != -1;
    }

    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROOMS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROOM_ID));
                String roomNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_NUMBER));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
                double rentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RENT_AMOUNT));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
                roomList.add(new Room(id, roomNumber, description, type, rentAmount, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomList;
    }

    public boolean updateRoom(int id, String roomNumber, String description, String type, double rentAmount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROOM_NUMBER, roomNumber);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_TYPE, type);
        values.put(COL_RENT_AMOUNT, rentAmount);
        values.put(COL_STATUS, status);
        int rows = db.update(TABLE_ROOMS, values, COL_ROOM_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteRoom(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ROOMS, COL_ROOM_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // ------------------ LEASE AGREEMENTS ------------------

    public boolean insertLeaseAgreement(int userId, int roomId, String leaseStart, String leaseEnd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEASE_USER_ID, userId);
        values.put(COL_LEASE_ROOM_ID, roomId);
        values.put(COL_LEASE_START, leaseStart);
        values.put(COL_LEASE_END, leaseEnd);
        long result = db.insert(TABLE_LEASE, null, values);
        if (result != -1) {
            ContentValues roomUpdate = new ContentValues();
            roomUpdate.put(COL_STATUS, "Leased");
            db.update(TABLE_ROOMS, roomUpdate, COL_ROOM_ID + "=?", new String[]{String.valueOf(roomId)});
            return true;
        }
        return false;
    }

    // Get all lease details including rent amount

    // Get lease details filtered by a specific userId (e.g., logged in user)
    public List<LeaseDetails> getLeaseDetailsByUserId(int userId) {
        List<LeaseDetails> leaseDetailsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT l.id, u.name AS userName, r.room_number AS roomNumber, r.rent_amount, " +
                "l.lease_start, l.lease_end " +
                "FROM " + TABLE_LEASE + " l " +
                "JOIN " + TABLE_USERS + " u ON l." + COL_LEASE_USER_ID + " = u." + COL_ID + " " +
                "JOIN " + TABLE_ROOMS + " r ON l." + COL_LEASE_ROOM_ID + " = r." + COL_ROOM_ID + " " +
                "WHERE l." + COL_LEASE_USER_ID + " = ? AND r." + COL_STATUS + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), "Leased"});

        if (cursor.moveToFirst()) {
            do {
                int leaseId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                String roomNumber = cursor.getString(cursor.getColumnIndexOrThrow("roomNumber"));
                double rentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("rent_amount"));
                String leaseStart = cursor.getString(cursor.getColumnIndexOrThrow("lease_start"));
                String leaseEnd = cursor.getString(cursor.getColumnIndexOrThrow("lease_end"));

                leaseDetailsList.add(new LeaseDetails(leaseId, userName, roomNumber, rentAmount, leaseStart, leaseEnd));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return leaseDetailsList;
    }


    // Add this method to update lease start and end dates
    public boolean updateLeaseDates(int leaseId, String newStart, String newEnd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEASE_START, newStart);
        values.put(COL_LEASE_END, newEnd);
        int rows = db.update(TABLE_LEASE, values, COL_LEASE_ID + "=?", new String[]{String.valueOf(leaseId)});
        return rows > 0;
    }

    // Add this method to insert a payment record
// Note: You need to add a payments table in your DB schema for this to work properly.
    public boolean insertPayment(int userId, int leaseId, String amount, String paymentDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create payments table if it does not exist yet
        String createPaymentsTable = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "lease_id INTEGER NOT NULL, " +
                "amount TEXT NOT NULL, " +
                "payment_date TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(lease_id) REFERENCES " + TABLE_LEASE + "(" + COL_LEASE_ID + "))";
        db.execSQL(createPaymentsTable);

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("lease_id", leaseId);
        values.put("amount", amount);
        values.put("payment_date", paymentDate);

        long result = db.insert("payments", null, values);
        return result != -1;
    }


}
