package com.myastrotell.pojo.response

import java.util.Comparator

class MyComparator : Comparator<AstrologerListResponse> {
    override fun compare(obj1: AstrologerListResponse?, obj2: AstrologerListResponse?): Int {
        if(obj1?.goodsSale==1 && obj2?.goodsSale==1){
            if (obj1?.sequence?.toInt()!! == obj2?.sequence?.toInt()!!)
                return 0
            //else if ((obj1.sequence?.toInt()!! > 0 && obj2.sequence?.toInt()!! > 0) && (obj1.sequence?.toInt()!! < obj2.sequence?.toInt()!!))
            else if (obj1.sequence?.toInt()!! > obj2.sequence?.toInt()!!)
                return 1
            else
                return -1

        }
        else
            return  0

    }
}