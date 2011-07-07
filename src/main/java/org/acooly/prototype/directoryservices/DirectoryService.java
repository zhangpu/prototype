package org.acooly.prototype.directoryservices;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

public class DirectoryService {

	static Logger logger = Logger.getLogger(DirectoryService.class);

	static final String DB_DIRVER = "com.mysql.jdbc.Driver";
	static final String DB_URI = "jdbc:mysql://localhost/prototype";
	static final String DB_USER = "root";
	static final String DB_PSWD = "820705";

	static final int TEST_COUNT = 100000;

	BasicDataSource dataSource = null;

	public static void main(String[] args) throws Exception {
		// BasicDataSource dataSource = new BasicDataSource();
		// dataSource.setDriverClassName(DB_DIRVER);
		// dataSource.setUrl(DB_URI);
		// dataSource.setUsername(DB_USER);
		// dataSource.setPassword(DB_PSWD);
		// dataSource.setInitialSize(5);
		// dataSource.setMaxActive(50);
		// dataSource.setMaxIdle(5);
		// dataSource.setMaxWait(5000);

		DirectoryService ds = new DirectoryService();
		// 生成测试数据
		// ds.generalTestData();
		// 测试
		ds.createDirTest();
		ds.queryPathTest();
		ds.deleteDirTest();
		ds.renameDirTest();
		ds.moveDirTest();
		ds.listDirTest();

		// dataSource.close();
	}

	public DirectoryService(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DirectoryService() {
		super();
	}

	
	int queryForder(String path){
		int result = 0;
		
		return result;
	}
	
	/**
	 * 测试创建目录
	 * 
	 * @throws Exception
	 */
	long createDirTest() throws Exception {

		Connection conn = getConn();

		long start = System.currentTimeMillis();
		Statement stmt = conn.createStatement();
		int parentId = RandomUtils.nextInt(100) + 1;
		int id = 0;
		stmt.execute("insert into folder(parentId,name) values(" + parentId + ",'folder_createTest')",
				Statement.RETURN_GENERATED_KEYS);

		long times = (System.currentTimeMillis() - start);
		logger.debug("Test create new dir : " + times + "ms");

		ResultSet rs = stmt.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		}
		stmt.execute("delete from folder where id = " + id);

		stmt.close();
		conn.close();
		return times;
	}

	/**
	 * 递归删除目录和文件测试，删除测试前，先备份删除的数据，测试完成后恢复
	 */
	long deleteDirTest() throws Exception {
		int id = RandomUtils.nextInt(1000) + 1; // 1-1000随机取

		// 备份需要删除的数据
		Connection conn = getConn();
		List<Integer> indexs = new ArrayList<Integer>(16);
		removeDirTest_querySubs(conn, indexs, id);
		List<String> backup = removeDirTest_backup(conn, indexs);
		// System.out.println("backup data:" + indexs);
		closeConn(conn);

		// 测试删除
		conn = getConn();
		long start = System.currentTimeMillis();
		removeDirTest_execute(conn, id);
		long time = (System.currentTimeMillis() - start);
		logger.debug("Test recursive delete id: " + id + "; sub nodes: " + indexs + ";  times:" + time + "ms");
		closeConn(conn);

		// 回复删除的数据
		conn = getConn();
		Statement stmt_backup = conn.createStatement();
		for (String sql : backup) {
			stmt_backup.addBatch(sql);
		}
		stmt_backup.executeBatch();
		closeConn(conn);
		// System.out.println("rollback data.");
		return time;

	}

	private void removeDirTest_execute(Connection conn, int id) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select id from folder where parentid = " + id);
		stmt.addBatch("delete from file where folderid = " + id);
		stmt.addBatch("delete from folder where id = " + id);
		while (rs.next()) {
			int subId = rs.getInt(1);
			if (subId != 0) {
				removeDirTest_execute(conn, subId);
			}
		}
		rs.close();
		stmt.executeBatch();
		stmt.close();
	}

	private void removeDirTest_querySubs(Connection conn, List<Integer> indexs, int id) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select id from folder where parentid = " + id);
		indexs.add(id);
		while (rs.next()) {
			int subId = rs.getInt(1);
			if (subId != 0) {
				removeDirTest_querySubs(conn, indexs, subId);
			}
		}
		rs.close();
		stmt.close();
	}

	private List<String> removeDirTest_backup(Connection conn, List<Integer> indexs) throws Exception {
		List<String> backup = new ArrayList<String>(32);
		for (int id : indexs) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from folder where id = " + id);
			if (rs.next()) {
				backup.add("insert into folder values(" + rs.getInt(1) + "," + rs.getInt(2) + ",'" + rs.getString(3)
						+ "')");
				ResultSet fileRs = stmt.executeQuery("select * from file where folderid = " + id);
				while (fileRs.next()) {
					backup.add("insert into file values(" + fileRs.getInt(1) + "," + fileRs.getInt(2) + ",'"
							+ fileRs.getString(3) + "')");
				}
				fileRs.close();
			}
			rs.close();
			stmt.close();
		}
		return backup;
	}

	/**
	 * 重命名测试
	 * 
	 * @throws Exception
	 */
	long renameDirTest() throws Exception {
		int id = RandomUtils.nextInt(10000) + 1;
		String newFolderName = UUID.randomUUID().toString();

		Connection conn = getConn();
		// 备份文件夹名称
		String folderName = "";
		Statement stmt = conn.createStatement();
		ResultSet r = stmt.executeQuery("select name from folder where id = " + id);
		if (r.next()) {
			folderName = r.getString(1);
		}
		r.close();
		stmt.close();

		// 测试rename
		long start = System.currentTimeMillis();
		stmt = conn.createStatement();
		stmt.execute("update folder set name = '" + newFolderName + "' where id = " + id);
		stmt.close();
		long time = (System.currentTimeMillis() - start);
		logger.debug("Test rename folderName id: " + id + " ; times:" + time + "ms");

		// 恢复数据
		stmt = conn.createStatement();
		stmt.execute("update folder set name = '" + folderName + "' where id = " + id);
		stmt.close();
		conn.close();
		return time;
	}

	/**
	 * 测试列表目录中的子目录和文件，单层
	 */
	long listDirTest() throws Exception {
		int id = RandomUtils.nextInt(10000) + 1;
		// id = 6361;
		Connection conn = getConn();

		long start = System.currentTimeMillis();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select id,name,'文件夹' from folder where parentid = " + id
				+ " union select id,name,'文件' from file where folderid=" + id);
		if (rs != null) {
			while (rs.next()) {
				;// System.out.println(rs.getInt(1) + "\t" + rs.getString(2) +
					// "\t" + rs.getString(3));
			}
			rs.close();
		}
		stmt.close();
		long time = (System.currentTimeMillis() - start);
		logger.debug("Test list folder id: " + id + " ; times:" + time + "ms");

		conn.close();
		return time;
	}

	/**
	 * 移动测试
	 * 
	 * @throws Exception
	 */
	long moveDirTest() throws Exception {
		int id = RandomUtils.nextInt(10000) + 1;

		Connection conn = getConn();
		conn.setAutoCommit(false);
		// 备份文件夹名称
		int oriParentId = 0;
		Statement stmt = conn.createStatement();
		ResultSet r = stmt.executeQuery("select parentid from folder where id = " + id);
		if (r.next()) {
			oriParentId = r.getInt(1);
		}
		r.close();
		stmt.close();

		// 测试move
		int newParentId = RandomUtils.nextInt(1000) + 10000;
		long start = System.currentTimeMillis();
		stmt = conn.createStatement();
		stmt.execute("update folder set parentid = '" + newParentId + "' where id = " + id);
		stmt.close();
		conn.commit();
		long time = (System.currentTimeMillis() - start);
		logger.debug("Test move folderName id: " + id + " ;parentId from " + oriParentId + " to " + newParentId
				+ "; times:" + time + "ms");

		// 恢复数据
		stmt = conn.createStatement();
		stmt.execute("update folder set parentid = '" + oriParentId + "' where id = " + id);
		stmt.close();
		conn.commit();
		conn.close();
		return time;
	}

	/**
	 * 查询全路径
	 * 
	 * @throws Exception
	 */
	long queryPathTest() throws Exception {
		long start = System.currentTimeMillis();
		Connection conn = getConn();
		// System.out.println("connect to mysql : " +
		// (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();

		int id = RandomUtils.nextInt(100) + 1;
		String path = getParent(conn, id);
		long time = (System.currentTimeMillis() - start);
		logger.debug("Test query node path (id:" + id + "); path: " + path + "; times: "
				+ time + "ms");
		closeConn(conn);
		return time;

	}

	private String getParent(Connection conn, int id) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select parentid,name from folder where id = " + id);
		String name = "";
		int parentid = 0;
		if (rs.next()) {
			name = rs.getString(2);
			parentid = rs.getInt(1);
		}
		rs.close();
		if (parentid != 0) {
			name = getParent(conn, parentid) + "/" + name;
		}
		stmt.close();
		return name;
	}

	/**
	 * 生成测试数据
	 * 
	 */
	static void generalTestData() throws Exception {

		// Class.forName(DB_DIRVER);
		// Connection conn = DriverManager.getConnection(DB_URI, DB_USER,
		// DB_PSWD);
		// Statement stmt = conn.createStatement();
		// stmt.execute("truncate table file");
		// stmt.execute("truncate table folder");
		File sqlFile = new File("d:/temp/test.sql");
		if (!sqlFile.exists()) {
			sqlFile.createNewFile();
		}
		RandomAccessFile file = new RandomAccessFile(sqlFile, "rw");

		Random random = new Random();
		int maxDirNum = 100000;

		int fileIndex = 1;
		int dirIndex = 1;
		while (dirIndex <= maxDirNum) {
			int dirDepth = random.nextInt(5) + 1;
			Integer parentId = null;
			for (int j = 0; j < dirDepth; j++) {
				file.writeBytes("insert into folder values(" + dirIndex + "," + parentId + ",'folder" + dirIndex
						+ "');\r\n");
				// stmt.addBatch("insert into folder values("+dirIndex+","+parentId+",'folder"+dirIndex+"')");
				int fileNumber = random.nextInt(5) + 1;
				for (int j2 = 0; j2 < fileNumber; j2++) {
					// stmt.addBatch("insert into file values("+fileIndex+","+dirIndex+",'file"+fileIndex+"')");
					file.writeBytes("insert into file values(" + fileIndex + "," + dirIndex + ",'file" + fileIndex
							+ "');\r\n");
					fileIndex++;
				}
				parentId = dirIndex;
				dirIndex++;
			}
			System.out.println("write dir count:" + dirIndex);
			// System.out.println("commit: "+stmt.executeBatch().length);
		}
		file.close();
		// stmt.close();
		// conn.close();

		System.out.println("general dir number: " + dirIndex);
		System.out.println("generate file number: " + fileIndex);
	}

	private Connection getConn() throws Exception {
		Class.forName(DB_DIRVER);
		Connection conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PSWD);
		return conn;
		// return dataSource.getConnection();
	}

	private void closeConn(Connection conn) throws Exception {
		conn.close();
	}

}
