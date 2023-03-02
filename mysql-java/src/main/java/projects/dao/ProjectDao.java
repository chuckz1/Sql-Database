package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	//list of all tables available
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	//inserts a project to correct tables
	public Project insertProject(Project project) {
		//create SQL statement
		// @formatter:0ff
		String sql = "" + "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) " 
				+ "VALUES " + "(?, ?, ?, ?, ?)";
		//@formatter:on
		
		//get connection
		try(Connection conn = DbConnection.getConnection()){
			//start a transaction
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				//set values for the transaction
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				//runs the SQL statement
				stmt.executeUpdate();
				
				//gets the project id and saves the entire transaction
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				//update project id in the project object and return it for reference later
				project.setProjectId(projectId);
				return project;
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
		
	}

}
