package com.xiaoshu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.Jiyun_studentMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.*;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class Jiyun_studentService {

	@Autowired(required = false)
	private Jiyun_studentMapper jiyun_studentMapper;
	@Autowired
	private CourseService courseService;

	// 查询所有
	public List<Jiyun_student> getAll() throws Exception {
//		Example example = new Example(Jiyun_student.class);
//		example.setOrderByClause("desc id");
//		return jiyun_studentMapper.selectByExample(example);
		return jiyun_studentMapper.selectAll();
	};

//	// 数量
//	public int countUser(User t) throws Exception {
//		return userMapper.selectCount(t);
//	};
//
//	// 通过ID查询
//	public User findOneUser(Integer id) throws Exception {
//		return userMapper.selectByPrimaryKey(id);
//	};
//
	// 新增
	public void addJiyun_student(Jiyun_student jiyun_student) throws Exception {
		jiyun_studentMapper.insert(jiyun_student);
	};
//
	// 修改
	public void updateJiyun_student(Jiyun_student jiyun_student) throws Exception {
		jiyun_studentMapper.updateByPrimaryKeySelective(jiyun_student);
	};
//
//	// 删除
//	public void deleteUser(Integer id) throws Exception {
//		userMapper.deleteByPrimaryKey(id);
//	};
//
//	// 登录
//	public User loginUser(User user) throws Exception {
//		UserExample example = new UserExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andPasswordEqualTo(user.getPassword()).andUsernameEqualTo(user.getUsername());
//		List<User> userList = userMapper.selectByExample(example);
//		return userList.isEmpty()?null:userList.get(0);
//	};
//
//	// 通过用户名判断是否存在，（新增时不能重名）
//	public User existUserWithUserName(String userName) throws Exception {
//		UserExample example = new UserExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andUsernameEqualTo(userName);
//		List<User> userList = userMapper.selectByExample(example);
//		return userList.isEmpty()?null:userList.get(0);
//	};
//
	// 通过角色判断是否存在
	public boolean existName(String name) throws Exception {
		List<Jiyun_student> all = jiyun_studentMapper.selectAll();
		for (Jiyun_student l:all) {
			if (l.getName().equals(name)){
				return false;
			}
		}
		return true;


	}

	public PageInfo<Jiyun_student> findUserPage(Sousuo sousuo, int pageNum, int pageSize, String ordername, String order) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		ordername = StringUtil.isNotEmpty(ordername)?ordername:"userid";
		order = StringUtil.isNotEmpty(order)?order:"desc";
		Example example = new Example(Jiyun_student.class);
		example.setOrderByClause(ordername+" "+order);
		example.and().andLike("name","%"+sousuo.getName()+"%");
		example.and().andLike("grade","%"+sousuo.getGrade()+"%");
		String begin=sousuo.getBegin();
		String end=sousuo.getEnd();
		Date begintime=null;
		Date endime=null;
		if (begin==""){
			begin="1800-01-01";
		}
		if (end==""){
			end="2500-01-01";
		}
		if (StringUtil.isNotEmpty(begin)){
			begintime=TimeUtil.ParseTime("begin","yyyy-MM-dd");
		}
		if (StringUtil.isNotEmpty(end)){
			endime=TimeUtil.ParseTime("end","yyyy-MM-dd");
		}
		example.and().andBetween("entrytime",begintime,endime);
		List<Jiyun_student> userList = jiyun_studentMapper.selectByExample(example);
		for (Jiyun_student l:userList) {
			Course course = courseService.getById(l.getCourse_id());
			l.setCourse(course);
		}
		PageInfo<Jiyun_student> pageInfo = new PageInfo<Jiyun_student>(userList);
		return pageInfo;
	}


}
