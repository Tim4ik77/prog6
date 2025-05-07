package ru.ifmo.lab6.server.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.collectionObject.*;
import ru.ifmo.lab6.server.exception.*;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudyGroupDataBaseService {
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(StudyGroupDataBaseService.class);

    private static final String TBL_STUDY_GROUPS = "study_groups";

    public StudyGroupDataBaseService(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Database connection cannot be null");
    }

    private static <E extends Enum<E>> String generateEnumCheckConstraint(String columnName, Class<E> enumClass, boolean nullable) {
        String values = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining("', '", "'", "'"));
        String constraint = String.format("%s IN (%s)", columnName, values);
        return nullable ? String.format("%s IS NULL OR %s", columnName, constraint) : constraint;
    }

    public void init() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TBL_STUDY_GROUPS + " (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL CHECK (name <> ''), " +
                "coordinates_x REAL NOT NULL, " +
                "coordinates_y BIGINT NOT NULL, " +
                "creation_date DATE NOT NULL, " +
                "students_count BIGINT NOT NULL CHECK (students_count > 0), " +
                "should_be_expelled INTEGER NOT NULL CHECK (should_be_expelled > 0), " +
                "transferred_students BIGINT NOT NULL CHECK (transferred_students > 0), " +
                "form_of_education TEXT CHECK (" + generateEnumCheckConstraint("form_of_education", FormOfEducation.class, true) + "), " +
                "group_admin_name TEXT CHECK (group_admin_name IS NULL OR group_admin_name <> ''), " +
                "group_admin_weight INTEGER CHECK (group_admin_weight IS NULL OR group_admin_weight > 0), " +
                "group_admin_eye_color TEXT CHECK (" + generateEnumCheckConstraint("group_admin_eye_color", Color.class, true) + "), " +
                "group_admin_hair_color TEXT CHECK (" + generateEnumCheckConstraint("group_admin_hair_color", Color.class, true) + "), " +
                "group_admin_nationality TEXT CHECK (" + generateEnumCheckConstraint("group_admin_nationality", Country.class, true) + "), " +
                "owner_login TEXT NOT NULL" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            logger.info("Table '{}' initialized successfully or already exists.", TBL_STUDY_GROUPS);
        } catch (SQLException ex) {
            logger.error("Failed to initialize table '{}': {}", TBL_STUDY_GROUPS, ex.getMessage(), ex);
            throw new CannotConnectToDataBaseException("Failed to initialize database table" + ex);
        }
    }

    public void addNewStudyGroup(StudyGroup studyGroup, String ownerLogin) throws CouldnotAddStudyGroupToDataBaseException, CannotConnectToDataBaseException {
        Objects.requireNonNull(studyGroup, "StudyGroup cannot be null");
        Objects.requireNonNull(ownerLogin, "Owner login cannot be null");

        String sqlInsert = "INSERT INTO " + TBL_STUDY_GROUPS + " (" +
                "name, coordinates_x, coordinates_y, creation_date, students_count, " +
                "should_be_expelled, transferred_students, form_of_education, group_admin_name, " +
                "group_admin_weight, group_admin_eye_color, group_admin_hair_color, " +
                "group_admin_nationality, owner_login" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement ps = connection.prepareStatement(sqlInsert)) {
            setStudyGroupStatementParameters(ps, studyGroup, ownerLogin, false);

            try (ResultSet generatedKeys = ps.executeQuery()) {
                if (generatedKeys.next()) {
                    studyGroup.setId(generatedKeys.getInt(1));
                    logger.info("Successfully added StudyGroup with ID {} for owner {}", studyGroup.getId(), ownerLogin);
                } else {
                    logger.error("Failed to add StudyGroup to database (no ID returned). Group: {}", studyGroup);
                    throw new CouldnotAddStudyGroupToDataBaseException("Database did not return an ID after insert.");
                }
            }
        } catch (SQLException ex) {
            handleSqlException(ex, "adding", studyGroup.getName(), ownerLogin);
            if (ex.getMessage().toLowerCase().contains("check constraint")) {
                throw new CouldnotAddStudyGroupToDataBaseException("Data validation failed: " + ex.getMessage());
            }
            throw new CannotConnectToDataBaseException("Database query failed during insert");
        } catch (IllegalArgumentException | NullPointerException ex) {
            logger.error("Invalid data provided for StudyGroup: {}", ex.getMessage());
            throw new CouldnotAddStudyGroupToDataBaseException("Invalid data for StudyGroup: " + ex.getMessage());
        }
    }

    public synchronized void updateStudyGroup(StudyGroup studyGroup, String ownerLogin) throws CannotUpdateStudyGroupException, CannotConnectToDataBaseException {
        Objects.requireNonNull(studyGroup, "StudyGroup cannot be null");
        Objects.requireNonNull(ownerLogin, "Owner login cannot be null");
        if (studyGroup.getId() <= 0) {
            throw new IllegalArgumentException("StudyGroup must have a valid ID to be updated.");
        }

        String sqlUpdate = "UPDATE " + TBL_STUDY_GROUPS + " SET " +
                "name = ?, coordinates_x = ?, coordinates_y = ?, " +
                "students_count = ?, should_be_expelled = ?, transferred_students = ?, " +
                "form_of_education = ?, group_admin_name = ?, group_admin_weight = ?, " +
                "group_admin_eye_color = ?, group_admin_hair_color = ?, group_admin_nationality = ? " +
                "WHERE id = ? AND owner_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            int paramIndex = setStudyGroupStatementParameters(ps, studyGroup, ownerLogin, true);
            ps.setInt(paramIndex++, studyGroup.getId());
            ps.setString(paramIndex, ownerLogin);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                logger.warn("Failed to update StudyGroup with ID {}. Not found or owner '{}' mismatch.", studyGroup.getId(), ownerLogin);
                throw new CannotUpdateStudyGroupException("StudyGroup not found or permission denied for ID: " + studyGroup.getId());
            } else {
                logger.info("Successfully updated StudyGroup with ID {}", studyGroup.getId());
            }
        } catch (SQLException ex) {
            handleSqlException(ex, "updating", String.valueOf(studyGroup.getId()), ownerLogin);
            if (ex.getMessage().toLowerCase().contains("check constraint")) {
                throw new CannotUpdateStudyGroupException("Data validation failed during update: " + ex.getMessage());
            }
            throw new CannotConnectToDataBaseException("Database query failed during update");
        } catch (IllegalArgumentException | NullPointerException ex) {
            logger.error("Invalid data provided for updating StudyGroup ID {}: {}", studyGroup.getId(), ex.getMessage());
            throw new CannotUpdateStudyGroupException("Invalid data for StudyGroup update: " + ex.getMessage());
        }
    }

    public synchronized void deleteStudyGroupById(int id, String ownerLogin) throws CannotDeleteFromDataBaseException, CannotConnectToDataBaseException {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive.");
        Objects.requireNonNull(ownerLogin, "Owner login cannot be null");

        String sqlDelete = "DELETE FROM " + TBL_STUDY_GROUPS + " WHERE id = ? AND owner_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sqlDelete)) {
            ps.setInt(1, id);
            ps.setString(2, ownerLogin);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                logger.warn("Failed to delete StudyGroup with ID {}. Not found or owner '{}' mismatch.", id, ownerLogin);
                throw new CannotDeleteFromDataBaseException("StudyGroup not found or permission denied for ID: " + id);
            } else {
                logger.info("Successfully deleted StudyGroup with ID {}", id);
            }
        } catch (SQLException ex) {
            handleSqlException(ex, "deleting", String.valueOf(id), ownerLogin);
            throw new CannotConnectToDataBaseException("Database query failed during delete");
        }
    }

    public synchronized int clearUserOwned(String ownerLogin) throws CannotConnectToDataBaseException {
        Objects.requireNonNull(ownerLogin, "Owner login cannot be null");
        String sqlDelete = "DELETE FROM " + TBL_STUDY_GROUPS + " WHERE owner_login = ?";
        int deletedCount = 0;

        try (PreparedStatement ps = connection.prepareStatement(sqlDelete)) {
            ps.setString(1, ownerLogin);
            deletedCount = ps.executeUpdate();
            logger.info("Cleared {} StudyGroups owned by user '{}'", deletedCount, ownerLogin);
        } catch (SQLException ex) {
            handleSqlException(ex, "clearing owned by", ownerLogin, null);
            throw new CannotConnectToDataBaseException("Database query failed during clear operation");
        }
        return deletedCount;
    }

    public synchronized void clearAll() throws CannotConnectToDataBaseException {
        String sqlTruncate = "TRUNCATE TABLE " + TBL_STUDY_GROUPS;
        logger.warn("Attempting to clear ALL entries from table '{}'", TBL_STUDY_GROUPS);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlTruncate);
            logger.info("Successfully cleared all entries from table '{}'", TBL_STUDY_GROUPS);
        } catch (SQLException ex) {
            handleSqlException(ex, "clearing all from", TBL_STUDY_GROUPS, null);
            throw new CannotConnectToDataBaseException("Database query failed during full clear operation");
        }
    }

    public ArrayList<StudyGroupWithOwner> loadCollection() throws CannotConnectToDataBaseException {
        ArrayList<StudyGroupWithOwner> loadedGroups = new ArrayList<>();
        String sqlSelectAll = "SELECT * FROM " + TBL_STUDY_GROUPS;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelectAll)) {

            while (rs.next()) {
                try {
                    StudyGroupWithOwner groupWithOwner = createStudyGroupFromResultSet(rs);
                    loadedGroups.add(groupWithOwner);
                } catch (SQLException | IllegalArgumentException | NullPointerException ex) {
                    int currentId = -1;
                    try { currentId = rs.getInt("id"); } catch (SQLException e) { /* ignore */ }
                    logger.error("Failed to parse StudyGroup with ID {} from database row: {}", currentId, ex.getMessage(), ex);
                    throw new CannotUploadCollectionException("Error parsing StudyGroup data from database (ID: " + currentId + "): " + ex.getMessage());
                }
            }
            logger.info("Successfully loaded {} StudyGroups from database.", loadedGroups.size());
        } catch (SQLException ex) {
            handleSqlException(ex, "loading collection from", TBL_STUDY_GROUPS, null);
            throw new CannotConnectToDataBaseException("Database query failed during collection load" + ex);
        }
        return loadedGroups;
    }


    private int setStudyGroupStatementParameters(PreparedStatement ps, StudyGroup sg, String ownerLogin, boolean isUpdate) throws SQLException {
        int i = 1;
        ps.setString(i++, sg.getName());
        ps.setFloat(i++, sg.getCoordinates().getX());
        ps.setLong(i++, sg.getCoordinates().getY());

        if (!isUpdate) {
            ps.setDate(i++, Date.valueOf(sg.getCreationDate()));
        }

        ps.setLong(i++, sg.getStudentsCount());
        ps.setInt(i++, sg.getShouldBeExpelled());
        ps.setLong(i++, sg.getTransferredStudents());

        ps.setObject(i++, sg.getFormOfEducation() != null ? sg.getFormOfEducation().name() : null, Types.VARCHAR);

        Person admin = sg.getGroupAdmin();
        if (admin != null) {
            ps.setString(i++, admin.getName());
            ps.setInt(i++, admin.getWeight());
            ps.setString(i++, admin.getEyeColor().name());
            ps.setString(i++, admin.getHairColor().name());
            ps.setObject(i++, admin.getNationality() != null ? admin.getNationality().name() : null, Types.VARCHAR);
        } else {
            ps.setNull(i++, Types.VARCHAR);
            ps.setNull(i++, Types.INTEGER);
            ps.setNull(i++, Types.VARCHAR);
            ps.setNull(i++, Types.VARCHAR);
            ps.setNull(i++, Types.VARCHAR);
        }

        if (!isUpdate) {
            ps.setString(i++, ownerLogin);
        }
        return i;
    }

    private StudyGroupWithOwner createStudyGroupFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String ownerLogin = Objects.requireNonNull(rs.getString("owner_login"), "DB returned null owner");

        Person groupAdmin = createPersonFromResultSet(rs, id);

        FormOfEducation formOfEducation = getNullableEnumFromResultSet(rs, "form_of_education", FormOfEducation.class, id);

        StudyGroup studyGroup = new StudyGroup(
                id,
                Objects.requireNonNull(rs.getString("name"), "DB returned null name"),
                new Coordinates(
                        rs.getFloat("coordinates_x"),
                        Objects.requireNonNull((Long) rs.getObject("coordinates_y"), "DB returned null coordinate Y")
                ),
                Objects.requireNonNull(rs.getDate("creation_date"), "DB returned null creation date").toLocalDate(),
                rs.getLong("students_count"),
                rs.getInt("should_be_expelled"),
                rs.getLong("transferred_students"),
                formOfEducation,
                groupAdmin
        );

        return new StudyGroupWithOwner(studyGroup, ownerLogin);
    }

    private Person createPersonFromResultSet(ResultSet rs, int studyGroupId) throws SQLException {
        String adminName = rs.getString("group_admin_name");
        if (rs.wasNull() || adminName == null) {
            return null;
        }

        Integer adminWeight = (Integer) Objects.requireNonNull(rs.getObject("group_admin_weight"),
                "DB returned null admin weight for non-null admin name, ID: " + studyGroupId);
        Color eyeColor = Objects.requireNonNull(getNullableEnumFromResultSet(rs, "group_admin_eye_color", Color.class, studyGroupId),
                "DB returned null admin eye color for non-null admin name, ID: " + studyGroupId);
        Color hairColor = Objects.requireNonNull(getNullableEnumFromResultSet(rs, "group_admin_hair_color", Color.class, studyGroupId),
                "DB returned null admin hair color for non-null admin name, ID: " + studyGroupId);
        Country nationality = getNullableEnumFromResultSet(rs, "group_admin_nationality", Country.class, studyGroupId);

        try {
            return new Person(adminName, adminWeight, eyeColor, hairColor, nationality);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SQLException("Failed to construct Person object from DB data for StudyGroup ID " + studyGroupId + ": " + e.getMessage(), e);
        }
    }

    private <T extends Enum<T>> T getNullableEnumFromResultSet(ResultSet rs, String columnName, Class<T> enumType, int entityId) throws SQLException {
        String enumStr = rs.getString(columnName);
        if (rs.wasNull() || enumStr == null || enumStr.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, enumStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid {} value '{}' found in database for entity ID {}. Treating as null.",
                    enumType.getSimpleName(), enumStr, entityId);
            return null;
        }
    }

    private void handleSqlException(SQLException ex, String operation, String target, String owner) {
        String ownerInfo = (owner != null) ? " by owner '" + owner + "'" : "";
        logger.error("SQL error while {} '{}'{}: {} (SQLState: {})",
                operation, target, ownerInfo, ex.getMessage(), ex.getSQLState(), ex);
    }
}