package com.xiaoshu.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.*;
import com.xiaoshu.service.*;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("student")
public class StudentController extends LogController{
	static Logger logger = Logger.getLogger(StudentController.class);

	@Autowired
	private Jiyun_studentService jiyun_studentService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private OperationService operationService;
	
	
	@RequestMapping("studentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Course> courses = courseService.getAll();
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("courses", courses);
		return "student";
	}
	
	
	@RequestMapping(value="studentList",method=RequestMethod.POST)
	public void userList(Sousuo sousuo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {

			String order = request.getParameter("order");
			String ordername = request.getParameter("ordername");
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Jiyun_student> userList= jiyun_studentService.findUserPage(sousuo,pageNum,pageSize,ordername,order);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	 //新增或修改
	@RequestMapping("reserveStudent")
	public void reserveUser(HttpServletRequest request,Jiyun_student jiyun_student,HttpServletResponse response){
		jiyun_student.setCreatetime(new Date());
		Integer id = jiyun_student.getId();
		JSONObject result=new JSONObject();
		try {
			if (id != null) {   // userId不为空 说明是修改

					jiyun_studentService.updateJiyun_student(jiyun_student);
					result.put("success", true);

			}else {   // 添加
				if(jiyun_studentService.existName(jiyun_student.getName())){  // 没有重复可以添加
					jiyun_studentService.addJiyun_student(jiyun_student);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}

//
//	@RequestMapping("deleteUser")
//	public void delUser(HttpServletRequest request,HttpServletResponse response){
//		JSONObject result=new JSONObject();
//		try {
//			String[] ids=request.getParameter("ids").split(",");
//			for (String id : ids) {
//				userService.deleteUser(Integer.parseInt(id));
//			}
//			result.put("success", true);
//			result.put("delNums", ids.length);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("删除用户信息错误",e);
//			result.put("errorMsg", "对不起，删除失败");
//		}
//		WriterUtil.write(response, result.toString());
//	}
//
//	@RequestMapping("editPassword")
//	public void editPassword(HttpServletRequest request,HttpServletResponse response){
//		JSONObject result=new JSONObject();
//		String oldpassword = request.getParameter("oldpassword");
//		String newpassword = request.getParameter("newpassword");
//		HttpSession session = request.getSession();
//		User currentUser = (User) session.getAttribute("currentUser");
//		if(currentUser.getPassword().equals(oldpassword)){
//			User user = new User();
//			user.setUserid(currentUser.getUserid());
//			user.setPassword(newpassword);
//			try {
//				userService.updateUser(user);
//				currentUser.setPassword(newpassword);
//				session.removeAttribute("currentUser");
//				session.setAttribute("currentUser", currentUser);
//				result.put("success", true);
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("修改密码错误",e);
//				result.put("errorMsg", "对不起，修改密码失败");
//			}
//		}else{
//			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
//			result.put("errorMsg", "对不起，原密码输入错误！");
//		}
//		WriterUtil.write(response, result.toString());
//	}
	@RequestMapping("addCourse")
	public String addCourse(Course course){
		course.setCreatetime(new Date());
		courseService.addCourse(course);
		return "redirect:studentIndex.htm?menuid=10";
	}
	@RequestMapping("out")
	public void out(HttpServletResponse response) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row1 = sheet.createRow(0);
		row1.createCell(0).setCellValue("编号");
		row1.createCell(1).setCellValue("课程名称");
		row1.createCell(2).setCellValue("课程编码");
		row1.createCell(3).setCellValue("创建时间");
		List<Course> all = courseService.getAll();
		for (Course l:all){
			int num = sheet.getLastRowNum();
			HSSFRow row = sheet.createRow(num + 1);
			row.createCell(0).setCellValue(l.getId());
			row.createCell(1).setCellValue(l.getName());
			row.createCell(2).setCellValue(l.getCode());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String format = simpleDateFormat.format(l.getCreatetime());
			row.createCell(3).setCellValue(format);
		}
		response.setHeader("Content-Disposition","attachment;filename=xuesheng.xls");
		response.setHeader("content-type","application/octer-stream");
		try {
			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
