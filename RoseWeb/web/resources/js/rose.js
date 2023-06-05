/* 
 * Copyright 2017 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


$(document).ready(function() {
    $(".datepicker").datepicker({
        dateFormat: 'mm-dd-yy',
        changeMonth: true,
        changeYear: true,
        yearRange: "1960:2017"
    });
    
    customReadyFunction();
});

PrimeFaces.locales['zh'] = {
    closeText: '关闭', 
    prevText: '上个月', 
    nextText: '下个月', 
    currentText: '今天', 
    monthNames: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'], 
    monthNamesShort: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'], 
    dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'], 
    dayNamesShort: ['日','一','二','三','四','五','六'], 
    dayNamesMin: ['日','一','二','三','四','五','六'], 
    weekHeader: '周', 
    firstDay: 1, 
    isRTL: false, 
    showMonthAfterYear: true, 
    yearSuffix: '', // 年 
    timeOnlyTitle: '仅时间', 
    timeText: '时间', 
    hourText: '时', minuteText: '分', secondText: '秒', ampm: false, month: '月', week: '周', day: '日', 
    allDayText : '全天' };

function openPrintableViewPage(viewLink){
    window.open(viewLink, "_blank", "toolbar=yes,scrollbars=yes,resizable=yes,top=50,left=50,width="+(screen.availWidth*0.75)+",height="+(screen.availHeight*0.75));
    return false;
}

function autoGrowTextArea(textarea) {
    if (textarea.clientHeight < textarea.scrollHeight) {
        textarea.style.height = textarea.scrollHeight + "px";
    }
}