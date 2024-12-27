package com.example.travel_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.databinding.FragmentRegionSearchBinding

class RegionSearchFragment : Fragment(), BigRegionAdapter.BigRegionClickListener {


    private var _binding: FragmentRegionSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegionSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onRegionClick(bigRegionName: String){
        val smallRegionList = getSmallRegionList(bigRegionName)
        showSmallRegionList(smallRegionList)
    }
    private fun showSmallRegionList(smallRegionList: ArrayList<SmallRegion>){
        val smallRecyclerView = binding.smallRegionRecycler
        smallRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        smallRecyclerView.adapter = SmallRegionAdapter(smallRegionList)
    }

    private fun getSmallRegionList(bigRegionName: String): ArrayList<SmallRegion>{
        return when(bigRegionName){
            "서울" -> {
                val seoulList = ArrayList<SmallRegion>()
                seoulList.add(SmallRegion("강남/역삼/삼성/논현"))
                seoulList.add(SmallRegion("서초/신사/방배"))
                seoulList.add(SmallRegion("잠실/방이"))
                seoulList.add(SmallRegion("잠실새내/신천/종합운동장"))
                seoulList.add(SmallRegion("영등포/여의도"))
                seoulList.add(SmallRegion("신림/서울대/사당/동작"))
                seoulList.add(SmallRegion("천호/길동/둔촌"))
                seoulList.add(SmallRegion("화곡/까치산/양천/목동"))
                seoulList.add(SmallRegion("구로/금천/오류/신도림"))
                seoulList.add(SmallRegion("신촌/홍대/합정"))
                seoulList.add(SmallRegion("연신내/불광/응암"))
                seoulList.add(SmallRegion("종로/대학로/동묘앞역"))
                seoulList.add(SmallRegion("성신여대/성북/월곡"))
                seoulList.add(SmallRegion("이태원/용산/서울역/명동/회현"))
                seoulList.add(SmallRegion("동대문/을지로/충무로/신당/약수"))
                seoulList.add(SmallRegion("회기/고려대/청량리/신설동"))
                seoulList.add(SmallRegion("장안동/답십리"))
                seoulList.add(SmallRegion("건대/군자/구의"))
                seoulList.add(SmallRegion("왕십리/성수/금호"))
                seoulList.add(SmallRegion("수유/미아"))
                seoulList.add(SmallRegion("상봉/중랑/면목"))
                seoulList.add(SmallRegion("태릉/노원/도봉/창동"))
                seoulList
            }
            "인천" -> {
                val incheonList = ArrayList<SmallRegion>()
                incheonList.add(SmallRegion("부평"))
                incheonList.add(SmallRegion("구월/소래포구/호구포"))
                incheonList.add(SmallRegion("서구(석남/서구청/검단)"))
                incheonList.add(SmallRegion("계양(작전/경인교대)"))
                incheonList.add(SmallRegion("주안"))
                incheonList.add(SmallRegion("송도/연수"))
                incheonList.add(SmallRegion("인천공항/을왕리/영종도"))
                incheonList.add(SmallRegion("동암/간석"))
                incheonList.add(SmallRegion("용현/숭의/월미도/신포/동인천/연안부두"))
                incheonList.add(SmallRegion("강화/옹진"))
                incheonList
            }
            "경기" -> {
                val kyeongkiList = ArrayList<SmallRegion>()
                kyeongkiList.add(SmallRegion("수원 인계동/나혜석거리"))
                kyeongkiList.add(SmallRegion("수원역/구운/행궁/장안구"))
                kyeongkiList.add(SmallRegion("수원시청/권선/영통/세류"))
                kyeongkiList.add(SmallRegion("안양/평촌/인덕원/과천"))
                kyeongkiList.add(SmallRegion("성남/분당/위례"))
                kyeongkiList.add(SmallRegion("용인"))
                kyeongkiList.add(SmallRegion("동탄/화성/오산/병점"))
                kyeongkiList.add(SmallRegion("하남/광주(퇴촌/곤지암)"))
                kyeongkiList.add(SmallRegion("여주/이천"))
                kyeongkiList.add(SmallRegion("안산 중앙역"))
                kyeongkiList.add(SmallRegion("안산 고잔/상록수/선부동/월피동"))
                kyeongkiList.add(SmallRegion("군포/의왕/금정/산본"))
                kyeongkiList.add(SmallRegion("시흥(월곶/정왕/오이도/거북섬)"))
                kyeongkiList.add(SmallRegion("광명"))
                kyeongkiList.add(SmallRegion("평택/송탄/안성"))
                kyeongkiList.add(SmallRegion("부천"))
                kyeongkiList.add(SmallRegion("일산/고양"))
                kyeongkiList.add(SmallRegion("파주"))
                kyeongkiList.add(SmallRegion("김포"))
                kyeongkiList.add(SmallRegion("의정부"))
                kyeongkiList.add(SmallRegion("구리"))
                kyeongkiList.add(SmallRegion("남양주(다산/별내/와부/호평)"))
                kyeongkiList.add(SmallRegion("남양주(오남/조안/화도/진접)"))
                kyeongkiList.add(SmallRegion("포천"))
                kyeongkiList.add(SmallRegion("양주/동두천/연천"))
                kyeongkiList.add(SmallRegion("양평"))
                kyeongkiList.add(SmallRegion("가평/청평"))
                kyeongkiList.add(SmallRegion("제부도/대부도"))



                kyeongkiList
            }
            "강원" -> {
                val kangwonList = ArrayList<SmallRegion>()
                kangwonList.add(SmallRegion("춘천/강촌"))
                kangwonList.add(SmallRegion("원주"))
                kangwonList.add(SmallRegion("경포대/사천/주문진/정동진"))
                kangwonList.add(SmallRegion("강릉역/교동/옥계"))
                kangwonList.add(SmallRegion("영월/정선"))
                kangwonList.add(SmallRegion("속초/고성"))
                kangwonList.add(SmallRegion("양양(서피비치/낙산)"))
                kangwonList.add(SmallRegion("동해/삼척/태백"))
                kangwonList.add(SmallRegion("평창"))
                kangwonList.add(SmallRegion("홍천/횡성"))
                kangwonList.add(SmallRegion("화천/철원/인제/양구"))

                kangwonList
            }
            "대전" -> {
                val daejeonList = ArrayList<SmallRegion>()
                daejeonList.add(SmallRegion("유성구"))
                daejeonList.add(SmallRegion("중구(은행/대흥/선화/유천)"))
                daejeonList.add(SmallRegion("동구(용전/복합터미널)"))
                daejeonList.add(SmallRegion("서구(둔산/용문/월평)"))
                daejeonList.add(SmallRegion("대덕구(중리/신탄진)"))
                daejeonList
            }
            "제주" -> {
                val jejuList = ArrayList<SmallRegion>()
                jejuList.add(SmallRegion("제주공항 서부(용담,도두,연동,노형동)"))
                jejuList.add(SmallRegion("제주공항 동부(제주시청,탑동,건입동)/추자도"))
                jejuList.add(SmallRegion("서귀포시/중문/모슬포"))
                jejuList.add(SmallRegion("이호테우/하귀/애월/한림/협재"))
                jejuList.add(SmallRegion("함덕/김녕/세화"))
                jejuList.add(SmallRegion("남원/표선/성산"))
                jejuList
            }
            "충북" -> {
                val chungbookList = ArrayList<SmallRegion>()
                chungbookList.add(SmallRegion("청주 흥덕구/서원구(청주 터미널)"))
                chungbookList.add(SmallRegion("청주 상당구/청원구(청주국제공항)"))
                chungbookList.add(SmallRegion("충주/수안보"))
                chungbookList.add(SmallRegion("제천/단양"))
                chungbookList.add(SmallRegion("진천/음성"))
                chungbookList.add(SmallRegion("보은/옥천/괴산/증평/영동"))
                chungbookList
            }
            "충남세종" -> {
                val chungnamList = ArrayList<SmallRegion>()
                chungnamList.add(SmallRegion("천안 서북구"))
                chungnamList.add(SmallRegion("천안 동남구"))
                chungnamList.add(SmallRegion("아산"))
                chungnamList.add(SmallRegion("공주/동학사/세종"))
                chungnamList.add(SmallRegion("계룡/금산/논산/청양"))
                chungnamList.add(SmallRegion("예산/홍성"))
                chungnamList.add(SmallRegion("태안/안면도"))
                chungnamList.add(SmallRegion("서산"))
                chungnamList.add(SmallRegion("당진"))
                chungnamList.add(SmallRegion("보령/대천해수욕장"))
                chungnamList.add(SmallRegion("서천/부여"))
                chungnamList
            }
            "부산" -> {
                val busanList = ArrayList<SmallRegion>()
                busanList.add(SmallRegion("해운대/센텀시티/재송"))
                busanList.add(SmallRegion("송정/기장/정관/이시리아 관광단지"))
                busanList.add(SmallRegion("광안리/수영"))
                busanList.add(SmallRegion("경성대/대연/용호동/문현"))
                busanList.add(SmallRegion("서면/양정/초읍/부산시민공원"))
                busanList.add(SmallRegion("남포동/중앙동/태종대/송도/영도"))
                busanList.add(SmallRegion("부산역/범일동/부산진역"))
                busanList.add(SmallRegion("연산/토곡"))
                busanList.add(SmallRegion("동래/사직/미남/온천장/부산대/구서/서동"))
                busanList.add(SmallRegion("사상(경전철)/엄궁/학장"))
                busanList.add(SmallRegion("덕천/화명/만덕/구포(구포역/KTX역)"))
                busanList.add(SmallRegion("하단/명지/김해공항/다대포/강서/신호/괴정/지사"))

                busanList
            }
            "울산" -> {
                val ulsanList = ArrayList<SmallRegion>()
                ulsanList.add(SmallRegion("남구/중구(삼산/성남/무거/신정)"))
                ulsanList.add(SmallRegion("동구/북구/울주군(일산/진장/진하/KTX역/영남알프스)"))
                ulsanList
            }
            "경남" -> {
                val kyungnamList = ArrayList<SmallRegion>()
                kyungnamList.add(SmallRegion("창원 상남동/용호동/중앙동/창원시청"))
                kyungnamList.add(SmallRegion("창원 명서동/봉곡동/팔용동/북면온천/창원종합버스터미널"))
                kyungnamList.add(SmallRegion("마산"))
                kyungnamList.add(SmallRegion("진해"))
                kyungnamList.add(SmallRegion("김해/장유"))
                kyungnamList.add(SmallRegion("양산/밀양"))
                kyungnamList.add(SmallRegion("진주"))
                kyungnamList.add(SmallRegion("거제/통영/고성"))
                kyungnamList.add(SmallRegion("사천/남해"))
                kyungnamList.add(SmallRegion("하동/산청/함양"))
                kyungnamList.add(SmallRegion("거창/함안/창녕/합천/의령"))
                kyungnamList
            }
            "대구" -> {
                val daeguList = ArrayList<SmallRegion>()
                daeguList.add(SmallRegion("동성로/서문시장/대구역/경북대/엑스코"))
                daeguList.add(SmallRegion("동대구역/신천동/수성못/범어/라이온즈파크/알파시티/시지"))
                daeguList.add(SmallRegion("대구공항/혁신도시/동촌유원지/팔공산/이시아폴리스/군위"))
                daeguList.add(SmallRegion("서대구역/북부정류장/평리/비산/칠곡지구/동천동/금호지구"))
                daeguList.add(SmallRegion("두류/이월드/본리동/죽전동/서부정류장/앞산공원/안지랑/대명동/봉덕동"))
                daeguList.add(SmallRegion("성서/계명대/상인동/대곡/현풍/테크노폴리스/가창/달성군"))
                daeguList
            }
            "경북" -> {
                val kyungbookList = ArrayList<SmallRegion>()
                kyungbookList.add(SmallRegion("포항/남구(시청/시외버스터미널/구룡포/쌍사/문덕/오천)"))
                kyungbookList.add(SmallRegion("포항/북구(영일대/죽도시장/여객터미널/송도)"))
                kyungbookList.add(SmallRegion("경주(보문단지/황리단길/불국사/양남/감포/안강)"))
                kyungbookList.add(SmallRegion("구미"))
                kyungbookList.add(SmallRegion("경산(영남대/대구대/갓바위/하양/진량/자인)"))
                kyungbookList.add(SmallRegion("안동(경북도청/하회마을)"))
                kyungbookList.add(SmallRegion("영천/청도"))
                kyungbookList.add(SmallRegion("김천/칠곡/성주"))
                kyungbookList.add(SmallRegion("문경/상주/영주/예천/의성/봉화"))
                kyungbookList.add(SmallRegion("울진/영덕/청송"))
                kyungbookList.add(SmallRegion("울릉도"))
                kyungbookList
            }
            "광주" -> {
                val gwangjuList = ArrayList<SmallRegion>()
                gwangjuList.add(SmallRegion("상무지구/금호지구/유스퀘어/서구"))
                gwangjuList.add(SmallRegion("충장로/대인시장/국립아시아문화전당/남구/동구"))
                gwangjuList.add(SmallRegion("첨단지구/양산동"))
                gwangjuList.add(SmallRegion("하남/광주여대/송정역/광산구"))
                gwangjuList.add(SmallRegion("광주역/기아챔피언스필드/전대사거리/북구"))
                gwangjuList
            }
            "전남" -> {
                val jeonnamList = ArrayList<SmallRegion>()
                jeonnamList.add(SmallRegion("여수"))
                jeonnamList.add(SmallRegion("순천"))
                jeonnamList.add(SmallRegion("광양"))
                jeonnamList.add(SmallRegion("목포"))
                jeonnamList.add(SmallRegion("무안/영암/신안"))
                jeonnamList.add(SmallRegion("나주/함평/영광/장성"))
                jeonnamList.add(SmallRegion("담양/곡성/화순/구례"))
                jeonnamList.add(SmallRegion("해남/완도/진도/강진/장흥/보성/고흥"))
                jeonnamList
            }
            "전주전북" -> {
                val jeonjuList = ArrayList<SmallRegion>()
                jeonjuList.add(SmallRegion("전주 덕진구"))
                jeonjuList.add(SmallRegion("전주 완산구/완주"))
                jeonjuList.add(SmallRegion("군산"))
                jeonjuList.add(SmallRegion("익산"))
                jeonjuList.add(SmallRegion("남원/임실/순창/무주/진안/장수"))
                jeonjuList.add(SmallRegion("정읍/부안/김제/고창"))
                jeonjuList
            }


            else -> ArrayList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bigRegionList = ArrayList<BigRegion>()
        bigRegionList.add(BigRegion("서울"))
        bigRegionList.add(BigRegion("인천"))
        bigRegionList.add(BigRegion("경기"))
        bigRegionList.add(BigRegion("강원"))
        bigRegionList.add(BigRegion("대전"))
        bigRegionList.add(BigRegion("제주"))
        bigRegionList.add(BigRegion("충남"))
        bigRegionList.add(BigRegion("충북"))
        bigRegionList.add(BigRegion("충남세종"))
        bigRegionList.add(BigRegion("울산"))
        bigRegionList.add(BigRegion("전주전북"))
        bigRegionList.add(BigRegion("광주"))
        bigRegionList.add(BigRegion("전남"))
        bigRegionList.add(BigRegion("대구"))
        bigRegionList.add(BigRegion("경북"))
        bigRegionList.add(BigRegion("부산"))
        bigRegionList.add(BigRegion("경남"))
        val recyclerView = binding.bigRegionRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BigRegionAdapter(bigRegionList, this)
    }
}