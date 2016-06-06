package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EUTreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.service.ContentCategoryService;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EUTreeNode> getCategoryList(Long parentId) {
		//根据parentId查询节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		//创建查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);		
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		ArrayList<EUTreeNode> resultList = new ArrayList<EUTreeNode>();
		for(TbContentCategory tbContentCategory : list){
			//创建一个节点
			EUTreeNode node = new EUTreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");			
			
			resultList.add(node);
		}		
		return resultList;
	}

	@Override
	public TaotaoResult insertContentCategory(Long parentId, String name) {
		//创建一个pojo
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		contentCategory.setParentId(parentId);
		contentCategory.setCreated(new Date());
		contentCategory.setIsParent(false);
		contentCategory.setSortOrder(1);
		//'状态。可选值:1(正常),2(删除)'
		contentCategory.setStatus(1);
		contentCategory.setUpdated(new Date());
		//添加记录
		contentCategoryMapper.insert(contentCategory);
		//查看父节点的isParent列是否为true，如果不是true改成true
		TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
		//判断是否为true
		if(!parentCat.getIsParent()){
			parentCat.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult deleteContentCategory(Long parentId, Long id) {		
		//删除该节点
		contentCategoryMapper.deleteByPrimaryKey(id);
		//查找是否还存在同样父节点的其他子节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> exampleList = contentCategoryMapper.selectByExample(example);
		//找到对应父节点
		TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
		//若不存在同样父节点的其他子节点，则修改IsParent字段为false
		if(parentCat.getIsParent() && exampleList.size() == 0){
			parentCat.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		if(parentCat.getIsParent() && exampleList.size() != 0){
			parentCat.setIsParent(false);
			TbContentCategoryExample example2 = new TbContentCategoryExample();
			Criteria criteria2 = example2.createCriteria();
			criteria2.andParentIdEqualTo(id);
			contentCategoryMapper.deleteByExample(example2);
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		TbContentCategory contentCat = contentCategoryMapper.selectByPrimaryKey(id);
		contentCat.setName(name);
		contentCategoryMapper.updateByPrimaryKey(contentCat);
		return TaotaoResult.ok(contentCat);
	}
}
