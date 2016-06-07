package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private TbContentMapper contentMapper;

	@Override
	public EUDataGridResult getContentList(int page, int rows, Long categoryId) {
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> contentList = contentMapper.selectByExampleWithBLOBs(example);		
		//分页处理
		PageHelper.startPage(page, rows);
		//创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(contentList);
		//取记录总条数
		PageInfo<TbContent> pageInfo = new PageInfo<>(contentList);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult insertContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		return TaotaoResult.ok(content);
	}

	@Override
	public TaotaoResult editContent(TbContent content) {
		TbContent content2 = contentMapper.selectByPrimaryKey(content.getId());
		content.setCreated(content2.getCreated());
		content.setUpdated(new Date());
		contentMapper.updateByPrimaryKey(content);
		return TaotaoResult.ok(content);
	}

	@Override
	public TaotaoResult deleteContent(long[] ids) {
		for(long id : ids){
			contentMapper.deleteByPrimaryKey(id);
		}

		return TaotaoResult.ok();
	}

}
