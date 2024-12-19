package com.lion.a08_memoapplication.fragment

import android.annotation.SuppressLint
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.a08_memoapplication.MainActivity
import com.lion.a08_memoapplication.R
import com.lion.a08_memoapplication.databinding.FragmentMemoSearchBinding
import com.lion.a08_memoapplication.databinding.RowText1Binding
import com.lion.a08_memoapplication.model.MemoModel
import com.lion.a08_memoapplication.repository.MemoRepository
import com.lion.a08_memoapplication.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MemoSearchFragment() : Fragment() {

    lateinit var fragmentMemoSearchBinding:FragmentMemoSearchBinding
    lateinit var mainActivity: MainActivity
    var memoSearchList = mutableListOf<MemoModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentMemoSearchBinding = FragmentMemoSearchBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingToolbarSearchMemo()
        settingTextField()
        settingRecyclerViewSearchStudent()
        //return inflater.inflate(R.layout.fragment_memo_search, container, false)

        return fragmentMemoSearchBinding.root
    }

    fun settingToolbarSearchMemo(){
        fragmentMemoSearchBinding.apply {
            toolbarSearchMemo.title = "메모 검색"

            toolbarSearchMemo.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarSearchMemo.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SEARCH_MEMO_FRAGMENT)
            }
        }
    }
    // 입력 요소 설정
    fun settingTextField(){
        fragmentMemoSearchBinding.apply {
            // 검색창에 포커스를 준다.
            mainActivity.showSoftInput(textFieldMemoSearchName.editText!!)
            // 키보드의 엔터를 누르면 동작하는 리스너
            textFieldMemoSearchName.editText?.setOnEditorActionListener { v, actionId, event ->
                // 검색 데이터를 가져와 보여준다.
                var keyword = textFieldMemoSearchName.editText?.text.toString()
                //Log.d("test111",keyword)
                //Log.d("test111","enterkey pressed")
                refreshRecyclerViewAfterSearch(keyword)



                mainActivity.hideSoftInput()
                true
            }
        }
    }
    // recyclerView를 구성하는 메서드
    fun settingRecyclerViewSearchStudent(){
        fragmentMemoSearchBinding.apply {
            recyclerViewSearchMemo.adapter = RecyclerViewMemoSearchAdapter()
            recyclerViewSearchMemo.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewSearchMemo.addItemDecoration(deco)
        }
    }

    inner class RecyclerViewMemoSearchAdapter : RecyclerView.Adapter<RecyclerViewMemoSearchAdapter.ViewHolderMemoSearch>(){
        inner class ViewHolderMemoSearch(val rowText1Binding: RowText1Binding) : RecyclerView.ViewHolder(rowText1Binding.root),
            OnClickListener {
            override fun onClick(v: View?) {
                // 메모 리스트를 보는 화면으로 이동한다.
                val dataBundle = Bundle()
                dataBundle.putInt("memoIdx", memoSearchList[adapterPosition].memoIdx)

                mainActivity.replaceFragment(FragmentName.READ_MEMO_FRAGMENT,
                    true, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMemoSearch {
            val rowText1Binding = RowText1Binding.inflate(layoutInflater, parent, false)
            val viewHolderMemoSearch = ViewHolderMemoSearch(rowText1Binding)
            rowText1Binding.root.setOnClickListener(viewHolderMemoSearch)
            return viewHolderMemoSearch
        }

        override fun getItemCount(): Int {
            return memoSearchList.size
        }

        override fun onBindViewHolder(holder: ViewHolderMemoSearch, position: Int) {
            holder.rowText1Binding.textViewRow.text = memoSearchList[position].memoTitle
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshRecyclerViewAfterSearch(keyword: String){
        fragmentMemoSearchBinding.apply {


            memoSearchList.clear()
            //var temptList = mutableListOf<MemoModel>()
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    // 데이터를 읽어온다.
                    MemoRepository.selectMemoDataAll(mainActivity)
                }
                var temptList = work1.await()
                // 데이터를 필터링한다.
                temptList.forEach{
                    //Log.d("test111","it.memoTitle : ${it.memoTitle}")
                    //Log.d("test111","keyword : ${keyword}")
                    if(it.memoTitle.contains(keyword) or it.memoText.contains(keyword)){
                        // 객체를 리스트에 담는다.
                        memoSearchList.add(it)
                        //Log.d("test111","검색 성공")
                    }
                }
                fragmentMemoSearchBinding.recyclerViewSearchMemo.adapter?.notifyDataSetChanged()

            }
        }



        // RecyclerView를 갱신한다.


    }





}