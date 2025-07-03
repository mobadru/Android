package com.example.semproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "baburental.db";
    public static final int DATABASE_VERSION = 1;
    public static final String COL_RENT_AMOUNT = "rentAmount";
    public static final String COL_LEASE_STATUS = "status";
    public static final String COL_PAYMENT_STATUS = "status";
    // Role constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TENANT = "TENANT";

    //notification
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COL_NOTIFICATION_ID = "id";
    public static final String COL_NOTIFICATION_TYPE = "type";
    public static final String COL_NOTIFICATION_MESSAGE = "message";
    public static final String COL_NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String COL_NOTIFICATION_IS_READ = "is_read";
    public static final String COL_NOTIFICATION_TARGET_USER_ID = "target_user_id";



    // USERS TABLE
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";

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
       //notification
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTIFICATION_TYPE + " TEXT NOT NULL, " +
                COL_NOTIFICATION_MESSAGE + " TEXT NOT NULL, " +
                COL_NOTIFICATION_TIMESTAMP + " TEXT NOT NULL, " +
                COL_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, " +
                COL_NOTIFICATION_TARGET_USER_ID + " INTEGER NOT NULL" +
                ")";
        db.execSQL(createNotificationsTable);

        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT, " +
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);


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

        if (result != -1) {
            // Fetch user name for notification message
            String userName = getUserNameById(userId);

            // Optionally, fetch room number or lease info if you want more details
            // For example, get room number by leaseId (implement getRoomNumberByLeaseId if needed)
            // String roomNumber = getRoomNumberByLeaseId(leaseId);

            String message = "Payment of TZS " + String.format("%.2f", amount) + " received from " + userName;
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            int adminId = getAdminUserId(); // Ensure this method returns the admin user ID

            insertNotification("payment", message, timestamp, adminId);
        }

        db.close();
        return result != -1;
    }



    // ------------------ USER CRUD ------------------

    public boolean insertUser(String name, String email, String phone, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
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



    public boolean insertUserAutoRole(String name, String email,String phone, String password) {
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
        return insertUser(name, email, password, phone,  role);
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
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
            user = new User(id, name, email, password, phone ,role);
        }
        cursor.close();
        db.close();
        return user;
    }

    // Fetch room number by room ID
    public String getRoomNumberById(int roomId) {
        String roomNumber = "Unknown Room";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOMS,
                    new String[]{COL_ROOM_NUMBER},
                    COL_ROOM_ID + "=?",
                    new String[]{String.valueOf(roomId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                roomNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_NUMBER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return roomNumber;
    }

    // Fetch user name by user ID
    public String getUserNameById(int userId) {
        String userName = "Unknown User";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_NAME},
                    COL_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return userName;
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
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));  // <-- Read phone
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
                userList.add(new User(id, name, email, phone, password, role));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }

    public boolean updateUser(int id, String name, String email, String password, String phone, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_PHONE, phone);  // <-- Update phone here
        values.put(COL_ROLE, role);
        int rows = db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;

    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_PASSWORD, user.getPassword()); // Consider hashing passwords!
        // Do not update COL_ID or COL_ROLE here usually, unless explicitly intended

        int rowsAffected = db.update(
                TABLE_USERS, // Assuming TABLE_USERS is defined
                values,
                COL_ID + " = ?", // Update where ID matches
                new String[]{String.valueOf(user.getId())}
        );
        db.close(); // Close database connection
        return rowsAffected > 0;
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

            // Fetch room number and user name for notification message
            String roomNumber = getRoomNumberById(roomId);
            String userName = getUserNameById(userId);

            String message = "New lease created for room " + roomNumber + " by user " + userName;
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            int adminId = getAdminUserId(); // Make sure this method returns the admin user ID
            insertNotification("lease", message, timestamp, adminId);

            db.close();
            return true;
        }
        db.close();
        return false;
    }


    public int getAdminUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int adminId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE " + COL_ROLE + " = ?", new String[]{ROLE_ADMIN});
        if (cursor.moveToFirst()) {
            adminId = cursor.getInt(0);
        }
        cursor.close();
        return adminId;
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
    public boolean insertMaintenanceRequest(int userId, int roomId, String description, String dateReported, String pending) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MAINTENANCE_USER_ID, userId);
        values.put(COL_MAINTENANCE_ROOM_ID, roomId);
        values.put(COL_MAINTENANCE_DESC, description);
        values.put(COL_MAINTENANCE_DATE, dateReported);
        values.put(COL_MAINTENANCE_STATUS, "Pending");

        long result = db.insert(TABLE_MAINTENANCE, null, values);

        if (result != -1) {
            // Fetch user name for notification message
            String userName = getUserNameById(userId);

            // Fetch room number for notification message
            String roomNumber = getRoomNumberById(roomId);

            String message = "New maintenance request from " + userName + " for room " + roomNumber;
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            int adminId = getAdminUserId(); // Ensure this method returns the admin user ID

            insertNotification("maintenance", message, timestamp, adminId);
        }

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


    public List<MaintenanceReport> getMaintenanceReportsByUserId(int tenantId) {
        List<MaintenanceReport> reports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m." + COL_MAINTENANCE_ID + ", m." + COL_MAINTENANCE_USER_ID + ", m." + COL_MAINTENANCE_ROOM_ID + ", " +
                "m." + COL_MAINTENANCE_DESC + ", m." + COL_MAINTENANCE_STATUS + ", m." + COL_MAINTENANCE_DATE + ", " +
                "r." + COL_ROOM_NUMBER +
                " FROM " + TABLE_MAINTENANCE + " m" +
                " LEFT JOIN " + TABLE_ROOMS + " r ON m." + COL_MAINTENANCE_ROOM_ID + " = r." + COL_ROOM_ID +
                " WHERE m." + COL_MAINTENANCE_USER_ID + " = ?" +
                " ORDER BY m." + COL_MAINTENANCE_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tenantId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MaintenanceReport report = new MaintenanceReport(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_ROOM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MAINTENANCE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM_NUMBER))  // room number from join
                );
                reports.add(report);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return reports;
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

    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COL_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));

                String phone = null;
                int phoneIndex = cursor.getColumnIndex(COL_PHONE);
                if (phoneIndex != -1) {
                    phone = cursor.getString(phoneIndex);
                }

                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));

                user = new User(id, name, email, phone, password, role);

                Log.d("DatabaseHelper", "User found: ID=" + id + ", Name=" + name);
            } else {
                Log.d("DatabaseHelper", "No user found with ID: " + userId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user by ID", e);
        } finally {
            if (cursor != null) cursor.close();
            // Do NOT close db here; managed by SQLiteOpenHelper
        }

        return user;
    }

    public boolean insertNotification(String type, String message, String timestamp, int targetUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIFICATION_TYPE, type);
        values.put(COL_NOTIFICATION_MESSAGE, message);
        values.put(COL_NOTIFICATION_TIMESTAMP, timestamp);
        values.put(COL_NOTIFICATION_IS_READ, 0);
        values.put(COL_NOTIFICATION_TARGET_USER_ID, targetUserId);
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        // Do NOT close db here; managed by SQLiteOpenHelper
        return result != -1;
    }
    public List<Notification> getNotificationsForAdmin(int adminUserId) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NOTIFICATIONS,
                null,
                COL_NOTIFICATION_TARGET_USER_ID + "=? OR " + COL_NOTIFICATION_TARGET_USER_ID + "=?",
                new String[]{String.valueOf(adminUserId), "0"}, // 0 means broadcast to all admins
                null,
                null,
                COL_NOTIFICATION_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_ID)));
                notification.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_TYPE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_MESSAGE)));
                notification.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_TIMESTAMP)));
                notification.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_IS_READ)) == 1);
                notification.setTargetUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIFICATION_TARGET_USER_ID)));
                notifications.add(notification);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return notifications;
    }

    // Add this method inside your DatabaseHelper class

    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIFICATION_IS_READ, 1); // Mark as read
        int rowsAffected = db.update(TABLE_NOTIFICATIONS, values, COL_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(notificationId)});
        // Do NOT close db here if you have multiple operations pending
        return rowsAffected > 0;
    }







}