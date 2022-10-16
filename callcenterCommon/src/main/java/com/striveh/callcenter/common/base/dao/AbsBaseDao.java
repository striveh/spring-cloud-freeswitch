/** */
package com.striveh.callcenter.common.base.dao;

import java.util.List;
import java.util.Map;

import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public abstract class AbsBaseDao<T extends BasePojo> {
	/** 日志 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	/** 单条sql操作模板 */
	protected SqlSessionTemplate template;
	/** 批量sql操作模板 */
	protected SqlSessionTemplate batchTemplate;

	/**
	 * @设置 单条sql操作模板
	 */
	protected abstract void setTemplate(SqlSessionTemplate template);

	/**
	 * @设置 批量sql操作模板
	 */
	protected abstract void setBatchTemplate(SqlSessionTemplate batchTemplate);

	/***
	 * 取得fullMapperId
	 * 
	 * @param mapperId
	 * @return
	 */
	public String getFullMapperId(String mapperId) {
		logger.info("====执行sql：" + this.getClass().getName() + "." + mapperId);
		return this.getClass().getName() + "." + mapperId;
	}

	/**
	 * 根据主键或唯一键查找数据
	 * 
	 * @param t
	 * @return
	 */
	public T selectUnique(T t) {
		return selectUnique("selectUnique", t);
	}

	/**
	 * 根据主键或唯一键查找数据
	 * 
	 * @param t
	 * @return
	 */
	public T selectUnique(String mapperId, T t) {
		return template.selectOne(getFullMapperId(mapperId), t);
	}

	/**
	 * 根据条件查询记录
	 * 
	 * @param t
	 * @return
	 */
	public List<T> selectList(T t) {
		return selectList("selectList", t);
	}

	/**
	 * 根据条件查询记录
	 * 
	 * @param t
	 * @return
	 */
	public List<T> selectList(String mapperId, T t) {
		boolean isPaging = t.getPageNum() != null && t.getPageSize() != null;
		if (isPaging) {
			PageHelper.startPage(t.getPageNum(), t.getPageSize(), true);
		}
		List<T> list = template.selectList(getFullMapperId(mapperId), t);
		if (isPaging) {
			PageInfo<T> pageInfo = new PageInfo<T>(list);
			t.setTotalRowCount(pageInfo.getTotal());
		}
		return list;
	}

	/**
	 * 根据条件查询记录
	 *
	 * @return
	 */
	public List<T> selectList(String mapperId, Map map) {
		return template.selectList(getFullMapperId(mapperId), map);
	}


	/**
	 * 根据条件查询记录2
	 *
	 * @return
	 */
	public List<T> selectList2(String mapperId, Long num) {
		return template.selectList(getFullMapperId(mapperId), num);
	}

	/**
	 * 插入一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int insert(T t) {
		return insert("insert", t);
	}

	/**
	 * 插入一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int insert(String mapperId, T t) {
		return template.insert(getFullMapperId(mapperId), t);
	}

	/**
	 * 修改一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int update(T t) {
		return update("update", t);
	}

	/**
	 * 修改一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int update(String mapperId, T t) {
		return template.update(getFullMapperId(mapperId), t);
	}

	/**
	 * 删除一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int delete(T t) {
		return delete("delete", t);
	}

	/**
	 * 删除一条数据
	 * 
	 * @param t
	 * @return
	 */
	public int delete(String mapperId, T t) {
		return template.delete(getFullMapperId(mapperId), t);
	}

	/**
	 * 批量插入数据
	 * 
	 * @param list
	 * @return
	 */
	public int insertList(List<T> list) {
		return insertList("insertList", list);
	}

	/**
	 * 批量插入数据
	 * 
	 * @param list
	 * @return
	 */
	public int insertList(String mapperId, List<T> list) {
		return batchOperation(mapperId, 1, list);
	}

	/**
	 * 批量修改数据
	 * 
	 * @param list
	 * @return
	 */
	public int updateList(List<T> list) {
		return updateList("update", list);
	}

	/**
	 * 批量修改数据
	 * 
	 * @param list
	 * @return
	 */
	public int updateList(String mapperId, List<T> list) {
		return batchOperation(mapperId, 2, list);
	}

	/**
	 * 批量删除数据
	 * 
	 * @param list
	 * @return
	 */
	public int deleteList(List<T> list) {
		return deleteList("delete", list);
	}

	/**
	 * 批量删除数据
	 * 
	 * @param list
	 * @return
	 */
	public int deleteList(String mapperId, List<T> list) {
		return batchOperation(mapperId, 3, list);
	}

	/**
	 * 批量操作
	 * 
	 * @param mapperId
	 * @param type 1插入、2修改、3删除
	 * @param list
	 * @return
	 */
	private int batchOperation(String mapperId, int type, List<T> list) {
		int i = 0;
		if (list != null && list.size() > 0) {
			SqlSession session = this.batchTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH);
			String fullMapperId = getFullMapperId(mapperId);
			for (T l : list) {
				if (type == 1) {
					session.insert(fullMapperId, l);
				} else if (type == 2) {
					session.update(fullMapperId, l);
				} else if (type == 3) {
					session.delete(fullMapperId, l);
				}
				i++;
				if (i % 100 == 0) {
					session.flushStatements();
				}
			}
			session.flushStatements();
		}
		return i;
	}

}
