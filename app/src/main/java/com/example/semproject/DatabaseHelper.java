package com.example.semproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserManagement.db";
    public static final int DATABASE_VERSION = 1;
    public static final String COL_RENT_AMOUNT = "rentAmount";
    public static final String COL_LEASE_STATUS = "status";
    public static final String COL_PAYMENT_STATUS = "status";
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

    public static final String COL_STATUS = "status";

    // LEASE AGREEMENT TABLE
    public static final String TABLE_LEASE = "leaseAgreement";
    public static final String COL_LEASE_ID = "id";
    public static final String COL_LEASE_USER_ID = "user_id";
    public static final String COL_LEASE_ROOM_ID = "room_id";
    public static final String COL_LEASE_START = "lease_start";
    public static final String COL_LEASE_END = "lease_end";

    // PAYMENTS TABLE
    public static final String TABLE_PAYMENTS = "payments";
    public static final String COL_PAYMENT_ID = "id";
    public static final String COL_PAYMENT_USER_ID = "user_id";
    public static final String COL_PAYMENT_LEASE_ID = "lease_id";
    public static final String COL_PAYMENT_AMOUNT = "amount";
    public static final String COL_PAYMENT_DATE = "payment_date";


    // MAINTENANCE REPORTS TABLE
    public static final String TABLE_MAINTENANCE = "maintenance_reports";
    public static final String COL_MAINTENANCE_ID = "id";
    public static final String COL_MAINTENANCE_USER_ID = "user_id";
    public static final String COL_MAINTENANCE_ROOM_ID = "room_id";
    public static final String COL_MAINTENANCE_DESC = "description";
    public static final String COL_MAINTENANCE_STATUS = "status";
    public static final String COL_MAINTENANCE_DATE = "date_reported";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_ROLE + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // Create Rooms Table
        String createRoomsTable = "CREATE TABLE " + TABLE_ROOMS + " (" +
                COL_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ROOM_NUMBER + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_TYPE + " TEXT NOT NULL, " +
                COL_RENT_AMOUNT + " REAL NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL)";
        db.execSQL(createRoomsTable);

        // Create Lease Agreement Table
        String createLeaseTable = "CREATE TABLE " + TABLE_LEASE + " (" +
                COL_LEASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LEASE_USER_ID + " INTEGER NOT NULL, " +
                COL_LEASE_ROOM_ID + " INTEGER NOT NULL, " +
                "rent_amount REAL, " +
                COL_LEASE_START + " TEXT, " +
                COL_LEASE_END + " TEXT, " +
                "status TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(" + COL_LEASE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_LEASE_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COL_ROOM_ID + "))";
        db.execSQL(createLeaseTable);


        // Create Payments Table
        String createPaymentsTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                COL_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PAYMENT_USER_ID + " INTEGER NOT NULL, " +
                COL_PAYMENT_LEASE_ID + " INTEGER NOT NULL, " +
                COL_PAYMENT_AMOUNT + " REAL NOT NULL, " +
                COL_PAYMENT_DATE + " TEXT NOT NULL, " +
                "status TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(" + COL_PAYMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_PAYMENT_LEASE_ID + ") REFERENCES " + TABLE_LEASE + "(" + COL_LEASE_ID + "))";
        db.execSQL(createPaymentsTable);

        String createMessagesTable = "CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_id INTEGER, " +
                "receiver_id INTEGER, " +
                "message TEXT, " +
                "timestamp TEXT" +
                ")";
        db.execSQL(createMessagesTable);


        String createMaintenance = "CREATE TABLE " + TABLE_MAINTENANCE + " (" +
                COL_MAINTENANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MAINTENANCE_USER_ID + " INTEGER, " +
                COL_MAINTENANCE_ROOM_ID + " INTEGER, " +
                COL_MAINTENANCE_DESC + " TEXT, " +
                COL_MAINTENANCE_STATUS + " TEXT DEFAULT 'Pending', " +
                COL_MAINTENANCE_DATE + " TEXT, " +
                "FOREIGN KEY(" + COL_MAINTENANCE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_MAINTENANCE_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COL_ROOM_ID + "))";
        db.execSQL(createMaintenance);

        // Insert default admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_NAME, "Admin");
        adminValues.put(COL_EMAIL, "admin@gmail.com");
        adminValues.put(COL_PASSWORD, "123"); // NOTE: For security consider hashing passwords!
        adminValues.put(COL_ROLE, ROLE_ADMIN);
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEASE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("ALTER TABLE payments ADD COLUMN status TEXT DEFAULT 'Pending'");


        onCreate(db);
    }


    // ----------- PAYMENT INSERTION --------------


    public boolean insertPayment(int userId, int leaseId, double amount, String paymentDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PAYMENT_USER_ID, userId);
        values.put(COL_PAYMENT_LEASE_ID, leaseId);
        values.put(COL_PAYMENT_AMOUNT, amount);
        values.put(COL_PAYMENT_DATE, paymentDate);
        long result = db.insert(TABLE_PAYMENTS, null, values);
        db.close();
        return result != -1;
    }



    // ------------------ USER CRUD ------------------

    public boolean insertUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_ROLE, (role == null || role.trim().isEmpty()) ? ROLE_TENANT : role);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public List<Payment> getPaymentsByUserId(int userId) {
        List<Payment> paymentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, user_id, lease_id, amount, payment_date, status FROM payments WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                int paymentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_ID));
                int uid = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_USER_ID));
                int leaseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_LEASE_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PAYMENT_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_DATE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_STATUS));

                paymentList.add(new Payment(paymentId, uid, leaseId, amount, date, status));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return paymentList;
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
        db.close();
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
        db.close();
        return rows > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USERS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
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
        db.close();
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
        db.close();
        return rows > 0;
    }

    public boolean deleteRoom(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ROOMS, COL_ROOM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // ------------------ LEASE AGREEMENTS ------------------

    public boolean insertLeaseAgreement(int userId, int roomId, double rentAmount, String leaseStart, String leaseEnd, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEASE_USER_ID, userId);
        values.put(COL_LEASE_ROOM_ID, roomId);
        values.put("rent_amount", rentAmount);
        values.put(COL_LEASE_START, leaseStart);
        values.put(COL_LEASE_END, leaseEnd);
        values.put("status", status);

        long result = db.insert(TABLE_LEASE, null, values);
        if (result != -1) {
            // Mark room as leased in rooms table
            ContentValues roomUpdate = new ContentValues();
            roomUpdate.put(COL_STATUS, "Leased");
            db.update(TABLE_ROOMS, roomUpdate, COL_ROOM_ID + "=?", new String[]{String.valueOf(roomId)});
            db.close();
            return true;
        }
        db.close();
        return false;
    }


    /**
     * Get lease details for a given user.
     */
    public List<LeaseDetails> getLeaseDetailsByUserId(int userId) {
        List<LeaseDetails> leaseDetailsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT l." + COL_LEASE_ID + ", u." + COL_NAME + " AS userName, r." + COL_ROOM_NUMBER + " AS roomNumber, r." + COL_RENT_AMOUNT + ", " +
                "l." + COL_LEASE_START + ", l." + COL_LEASE_END + ", r." + COL_ROOM_ID + ", l." + COL_LEASE_STATUS +
                " FROM " + TABLE_LEASE + " l " +
                "JOIN " + TABLE_USERS + " u ON l." + COL_LEASE_USER_ID + " = u." + COL_ID + " " +
                "JOIN " + TABLE_ROOMS + " r ON l." + COL_LEASE_ROOM_ID + " = r." + COL_ROOM_ID + " " +
                "WHERE l." + COL_LEASE_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int leaseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LEASE_ID));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                String roomNumber = cursor.getString(cursor.getColumnIndexOrThrow("roomNumber"));
                double rentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RENT_AMOUNT));  // <--- Use COL_RENT_AMOUNT constant
                String leaseStart = cursor.getString(cursor.getColumnIndexOrThrow(COL_LEASE_START));
                String leaseEnd = cursor.getString(cursor.getColumnIndexOrThrow(COL_LEASE_END));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_LEASE_STATUS));
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROOM_ID));

                LeaseDetails lease = new LeaseDetails(leaseId, userName, roomNumber, rentAmount, leaseStart, leaseEnd, roomId, status);
                leaseDetailsList.add(lease);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return leaseDetailsList;
    }





    // Insert maintenance report
    public boolean insertMaintenanceRequest(int userId, int roomId, String description, String dateReported) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MAINTENANCE_USER_ID, userId);
        values.put(COL_MAINTENANCE_ROOM_ID, roomId);
        values.put(COL_MAINTENANCE_DESC, description);
        values.put(COL_MAINTENANCE_DATE, dateReported);
        values.put(COL_MAINTENANCE_STATUS, "Pending");
        long result = db.insert(TABLE_MAINTENANCE, null, values);
        db.close();
        return result != -1;
    }

    // Get all maintenance reports (for admin)
    public List<MaintenanceReport> getAllMaintenance() {
        List<MaintenanceReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m." + COL_MAINTENANCE_ID + ", u." + COL_NAME + ", r." + COL_ROOM_NUMBER + ", " +
                "m." + COL_MAINTENANCE_DESC + ", m." + COL_MAINTENANCE_STATUS + ", m." + COL_MAINTENANCE_DATE +
                " FROM " + TABLE_MAINTENANCE + " m " +
                "JOIN " + TABLE_USERS + " u ON m." + COL_MAINTENANCE_USER_ID + " = u." + COL_ID + " " +
                "JOIN " + TABLE_ROOMS + " r ON m." + COL_MAINTENANCE_ROOM_ID + " = r." + COL_ROOM_ID +
                " ORDER BY m." + COL_MAINTENANCE_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String tenantName = cursor.getString(1);
                String roomNumber = cursor.getString(2);
                String description = cursor.getString(3);
                String status = cursor.getString(4);
                String date = cursor.getString(5);
                list.add(new MaintenanceReport(id, tenantName, roomNumber, description, status, date));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public boolean updateMaintenanceRequest(int requestId, String newDescription, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", newDescription);
        values.put("status", newStatus); // Optional: Only if status change is allowed

        int rows = db.update("maintenance_reports", values, "id = ?", new String[]{String.valueOf(requestId)});
        db.close();
        return rows > 0;
    }

    // In DatabaseHelper.java, add:

    public List<MaintenanceReport> getMaintenanceReportsByUserIdAndStatus(int tenantId, String status) {
        List<MaintenanceReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m." + COL_MAINTENANCE_ID + ", m." + COL_MAINTENANCE_USER_ID + ", m." + COL_MAINTENANCE_ROOM_ID + ", " +
                "m." + COL_MAINTENANCE_DESC + ", m." + COL_MAINTENANCE_STATUS + ", m." + COL_MAINTENANCE_DATE + " " +
                "FROM " + TABLE_MAINTENANCE + " m " +
                "WHERE m." + COL_MAINTENANCE_USER_ID + " = ? AND m." + COL_MAINTENANCE_STATUS + " = ? " +
                "ORDER BY m." + COL_MAINTENANCE_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tenantId), status});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int userId = cursor.getInt(1);
                int roomId = cursor.getInt(2);
                String description = cursor.getString(3);
                String statusText = cursor.getString(4);
                String date = cursor.getString(5);

                MaintenanceReport report = new MaintenanceReport(id, userId, roomId, description, statusText, date);
                list.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<LeaseDetails> getAllLeases() {
        List<LeaseDetails> leases = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT l.id, u.name AS userName, r.room_number AS roomNumber, " +
                "l.rent_amount, l.lease_start, l.lease_end, r.id, l.status " + // added r.id (room ID)
                "FROM leaseAgreement l " +
                "JOIN users u ON l.user_id = u.id " +
                "JOIN rooms r ON l.room_id = r.id";
        Cursor cursor = db.rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                int leaseId = cursor.getInt(0);
                String username = cursor.getString(1);
                String roomNumber = cursor.getString(2);
                double rentAmount = cursor.getDouble(3);
                String start = cursor.getString(4);
                String end = cursor.getString(5);
                int roomId = cursor.getInt(6);
                String status = cursor.getString(7);

                LeaseDetails lease = new LeaseDetails(leaseId, username, roomNumber, rentAmount, start, end, roomId, status);
                leases.add(lease);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return leases;
    }


    public boolean updateLease(LeaseDetails lease) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_LEASE_START, lease.getLeaseStart());
        values.put(COL_LEASE_END, lease.getLeaseEnd());

        int rows = db.update(TABLE_LEASE, values, COL_LEASE_ID + " = ?", new String[]{String.valueOf(lease.getLeaseId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteLease(int leaseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_LEASE, COL_LEASE_ID + " = ?", new String[]{String.valueOf(leaseId)});
        db.close();
        return rows > 0;
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        List<Payment> paymentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PAYMENTS, null);

        if (cursor.moveToFirst()) {
            do {
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                int paymentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_USER_ID));
                int leaseId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_LEASE_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PAYMENT_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_DATE));

                paymentList.add(new Payment(paymentId, userId, leaseId, amount, date, status));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return paymentList;
    }

    // Update a payment
    public boolean updatePayment(int paymentId, double newAmount, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PAYMENT_AMOUNT, newAmount);
        values.put(COL_PAYMENT_DATE, newDate);
        int rows = db.update(TABLE_PAYMENTS, values, COL_PAYMENT_ID + " = ?", new String[]{String.valueOf(paymentId)});
        db.close();
        return rows > 0;
    }

    // Delete a payment
    public boolean deletePayment(int paymentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PAYMENTS, COL_PAYMENT_ID + " = ?", new String[]{String.valueOf(paymentId)});
        db.close();
        return rows > 0;
    }

    // Get all maintenance reports
    public List<MaintenanceReport> getAllMaintenanceReports() {
        List<MaintenanceReport> reports = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT m.id, m.description, m.status, m.date_reported, r.room_number " +
                "FROM maintenance_reports m JOIN rooms r ON m.room_id = r.id";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String desc = cursor.getString(1);
                String status = cursor.getString(2);
                String date = cursor.getString(3);
                String room = cursor.getString(4);
                reports.add(new MaintenanceReport(id, "", room, desc, status, date));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    // Update only maintenance status
    public boolean updateMaintenanceStatus(int id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MAINTENANCE_STATUS, status);
        int rows = db.update(TABLE_MAINTENANCE, values, COL_MAINTENANCE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // Delete maintenance record
    public boolean deleteMaintenance(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_MAINTENANCE, COL_MAINTENANCE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean updatePaymentStatus(int paymentId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rows = db.update(TABLE_PAYMENTS, values, COL_PAYMENT_ID + " = ?", new String[]{String.valueOf(paymentId)});
        db.close();
        return rows > 0;
    }

    public boolean updateLeaseStatus(int leaseId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int rows = db.update(TABLE_LEASE, values, COL_LEASE_ID + " = ?", new String[]{String.valueOf(leaseId)});
        db.close();
        return rows > 0;
    }



    public boolean insertMessage(int senderId, int receiverId, String message, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_id", senderId);
        values.put("receiver_id", receiverId);
        values.put("message", message);
        values.put("timestamp", timestamp);

        long result = db.insert("messages", null, values);
        return result != -1;
    }

    public int getOwnerId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int ownerId = -1;

        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE role = 'Admin' LIMIT 1", null);
        if (cursor.moveToFirst()) {
            ownerId = cursor.getInt(0); // Fetch the first Admin ID
        }
        cursor.close();
        return ownerId;
    }


    public boolean updateLeaseDates(int leaseId, String startDate, String endDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEASE_START, startDate);
        values.put(COL_LEASE_END, endDate);
        values.put("status", status);

        int rows = db.update(TABLE_LEASE, values, COL_LEASE_ID + " = ?", new String[]{String.valueOf(leaseId)});
        db.close();
        return rows > 0;
    }


}