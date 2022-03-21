const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

function symbolsearch(form) {
    var symbol = form.symbol.value;
    getProfile(symbol);
    getQuote(symbol);
    getTrend(symbol);
    getCandle(symbol);
    getNews(symbol);

    return false;
}

function getProfile(symbol) {
    fetch("/profile?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    if(data.name === undefined) {
                        showError();
                    }
                    else {
                        document.getElementById("company-logo").src = data.logo;
                        document.getElementById("company-name").innerText = data.name;
                        var tickerEle = document.getElementsByClassName("stock-ticker-symbol");
                        for(var i = 0; i < tickerEle.length; i++) {
                            tickerEle[i].innerText = data.ticker;
                        }
                        document.getElementById("stock-exchange-code").innerText = data.exchange;
                        document.getElementById("company-start-date").innerText = data.ipo;
                        document.getElementById("category").innerText = data.finnhubIndustry;

                        showTab();
                    }
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function getQuote(symbol) {
    fetch("/quote?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    var date = formattedDate(new Date(data.t*1000));
                    document.getElementById("trading-day").innerText = date;
                    document.getElementById("prev-closing-price").innerText = data.pc;
                    document.getElementById("opening-price").innerText = data.o;
                    document.getElementById("high-price").innerText = data.h;
                    document.getElementById("low-price").innerText = data.l;
                    document.getElementById("change").innerHTML = data.d + arrow(data.d);
                    document.getElementById("change-percent").innerHTML = data.dp + arrow(data.dp);
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function arrow(data) {
    if(data < 0) {
        return " <img src=\"/static/images/RedArrowDown.png\" width=\"15px\" height=\"15px\">"; 
    } else if (data > 0){
        return " <img src=\"/static/images/GreenArrowUp.png\" width=\"15px\" height=\"15px\">"; 
    }
    return "";
}

function getTrend(symbol) {
    fetch("/trend?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    document.getElementById("ss").src = data[0].strongSell;
                    document.getElementById("s").innerText = data[0].sell;
                    document.getElementById("h").innerText = data[0].hold;
                    document.getElementById("b").innerText = data[0].buy;
                    document.getElementById("sb").innerText = data[0].strongBuy;
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function getCandle(symbol) {
    fetch('/candle?symbol=' + symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went wrong..." + response.status);
                return;
            }
            
            response.json().then (
                function(data) {
                    var priceArray = data.t.map(function(ele, index) {
                        return [ele*1000, data.c[index]];
                    });

                    var volumeArray = data.t.map(function(ele, index) {
                        return [ele*1000, data.v[index]];
                    });

                    var today = new Date();
                    today.setMonth(today.getMonth() -6);
                    today.setDate(today.getDate() -1);
                    var fromDate = new Date(today);
                    
                    Highcharts.stockChart("chart-container", {
                            title: {
                                text: 'Stock Price ' + symbol + " " + formattedDate2(fromDate)
                            },
                    
                            subtitle: {
                                text: '<a href="https://finnhub.io/" class="subtitle" target="_blank">Source: Finnhub</a>'
                            },
                    
                            xAxis: {
                                gapGridLineWidth: 0
                            },

                            yAxis: [{
                                title: {
                                    text:'Stock Price'
                                },
                                opposite: false
                            },{
                                title: {
                                    text:'Volume'
                                }
                            }],
                    
                            rangeSelector: {
                                buttons: [
                                {
                                    type: 'day',
                                    count: 7,
                                    text: '7d'
                                }, 
                                {
                                    type: 'day',
                                    count: 15,
                                    text: '15d'
                                }, 
                                {
                                    type: 'month',
                                    count: 1,
                                    text: '1m'
                                }, 
                                {
                                    type: 'month',
                                    count: 3,
                                    text: '3m'
                                }, 
                                {
                                    type: 'month',
                                    count: 6,
                                    text: '6m'
                                }],
                                selected: 0,
                                inputEnabled: false
                            },
                    
                            series: [{
                                name: 'Stock Price',
                                type: 'area',
                                data: priceArray,
                                pointPlacement: 'on',
                                gapSize: 5,
                                tooltip: {
                                    valueDecimals: 2
                                },
                                fillColor: {
                                    linearGradient: {
                                        x1: 0,
                                        y1: 0,
                                        x2: 0,
                                        y2: 1
                                    },
                                    stops: [
                                        [0, Highcharts.getOptions().colors[0]],
                                        [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                    ]
                                },
                                threshold: null
                            },
                            {
                                name: 'Volume',
                                type: 'column',
                                data: volumeArray,
                                yAxis: 1,
                                pointPlacement: 'on',
                                pointWidth: 3
                            }
                        ]
                        
                        });                    
                }
            );
        }
    ).catch(
        function(err) {
            console.log("Fetch err: -S", err);
        }
    )
}

function getNews(symbol) {
    fetch("/news?symbol="+symbol).then(
        function(response) {
            if(response.status!=200) {
                console.log("Something went wrong..." + response.status);
                return;
            }

            response.json().then(
                function(data) {
                    var newsTab = document.getElementById("news");
                    var innerHTML = "";
                    for(var i = 0, j = 0; j < data.length, i < 5; i++, j++) {
                        while(j<data.length && dataMissing(data[j])) j++;
                        var img = "<img src=\""+data[j].image+"\" width=\"100px\" height=\"100px\">";
                        var date = formattedDate(new Date(data[j].datetime*1000));
                        var div = "<div><span class=\"bold\">"+data[j].headline+"</span><br>"+date+"<br><a href=\""+data[j].url+"\">See Original Post</a></div>";
                        innerHTML += '<div class="news-block">'+img+div+'</div>';
                    } 
                    newsTab.innerHTML = innerHTML;
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function dataMissing(data) {
    if(data.image == undefined || data.image == "" ||
        data.headline == undefined || data.headline == "" ||
        data.url == undefined || data.url == "" ||
        data.datetime == undefined) {
            return true;
        }
    return false;
}

function clearInput() {
    document.getElementById("symbol").value = "";
    document.getElementById("error-message").style.display = "none";
}

function showTab() {
    document.getElementById("error-message").style.display = "none";
    if(document.getElementById("tab-container").style.display == "none") {
        document.getElementById("tab-container").style.display = "inline-block";
        document.getElementById("company-btn").className += " active";
        document.getElementById("company").style.display = "block";
    }
}

function showError() {
    document.getElementById("error-message").style.display = "inline-block";
    document.getElementById("tab-container").style.display = "none";
    closeTabs();
}

function closeTabs() {
    var tabContents = document.getElementsByClassName("tabcontent");
    for(var i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = "none";
    }

    var tabLinks = document.getElementsByClassName("tablinks");
    for(var i=0; i < tabLinks.length; i++) {
        tabLinks[i].className = tabLinks[i].className.replace(" active","");
    }
}

function openTab(btn, tabName) {
    closeTabs();
    document.getElementById(tabName).style.display = "block";
    btn.className += " active";
}

function formattedDate(date) {
    var day = date.getDate();
    var month = months[date.getMonth()];
    var year = date.getFullYear();
    return day + " " + month + ", " + year;
}

function formattedDate2(date) {
    var day = date.getDate();
    var month = date.getMonth() +1;
    var year = date.getFullYear();
    return year+"-"+month+"-"+day;
}