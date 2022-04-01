import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendHelperService } from '../backend-helper.service';
import { CandleDAO } from '../dao/candle-dao';
import { EarningsDAO } from '../dao/earnings-dao';
import { NewsDAO } from '../dao/news-dao';
import { Profile2DAO } from '../dao/profile2-dao';
import { QuoteDAO } from '../dao/quote-dao';
import { RecommendationDAO } from '../dao/recommendation-dao';
import { SocialDAO } from '../dao/social-dao';

import * as Highcharts from 'highcharts/highstock';
import { Options } from 'highcharts/highstock';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NewsComponent } from '../news/news.component';
import { TransactionComponent } from '../transaction/transaction.component';

declare var require: any;
require('highcharts/indicators/indicators')(Highcharts);
require('highcharts/indicators/volume-by-price')(Highcharts);

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  symbol:string = '';
  closedTime:string = '';
  currentTime: string = '';
  marketOpen:boolean = true;
  validTicker:boolean = true;
  quote:QuoteDAO|undefined;
  profile2:Profile2DAO|undefined;
  candle:CandleDAO|undefined;
  hourlyCandle:CandleDAO|undefined;
  historicalCandle:CandleDAO|undefined;
  peers:[]|undefined;
  topNews:NewsDAO[]|undefined;
  recommendation: RecommendationDAO[]|undefined;
  social: SocialDAO|undefined;
  earnings: EarningsDAO[]|undefined;
  
  Highcharts = Highcharts;
  hourlyChartOptions: Options = {} as Options;
  historicalChartOptions: Options = {} as Options;

  constructor(
    private route: ActivatedRoute,
    private backendHelper: BackendHelperService,
    private newsModalService: NgbModal,
    private transactionService: NgbModal
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.clearData();
      this.symbol = params.get('ticker')??' ';
      this.checkExist(this.symbol);
    });
  }

  openTransactionModal(ticker:string, name: string, price:number, isBuy:boolean) {
    const transactionModal = this.transactionService.open(TransactionComponent);
    transactionModal.componentInstance.ticker = ticker;
    transactionModal.componentInstance.name = name;
    transactionModal.componentInstance.price = price;
    transactionModal.componentInstance.isBuy = isBuy;
  }

  openNewsDetail(news: NewsDAO){
    const newsModal = this.newsModalService.open(NewsComponent);
    newsModal.componentInstance.news = news;
  }

  createHistoricalChart() {
    let ohlc = this.historicalCandle?.t.map((val, index) => {
      return [val*1000, 
              this.historicalCandle?.o[index],
              this.historicalCandle?.h[index],
              this.historicalCandle?.l[index],
              this.historicalCandle?.c[index],
            ];
    });

    let v = this.historicalCandle?.t.map((val, index) => {
      return [val*1000,
              this.historicalCandle?.v[index]
            ];
    });

    this.historicalChartOptions = {
      series: [
        {
          data: ohlc,
          type: 'candlestick',
          name: this.symbol,
          id: this.symbol,
          zIndex:2
        },
        {
          data: v,
          type: 'column',
          name: 'Volume',
          id: 'volume',
          yAxis: 1
        },
        {
          type: 'vbp',
          linkedTo: this.symbol,
          params: {
            volumeSeriesID: 'volume'
          },
          dataLabels: {
            enabled: false
          },
          zoneLines: {
            enabled: false
          }

        },
        {
          type: 'sma',
          linkedTo: this.symbol,
          zIndex: 1,
          marker: {
            enabled: false
          }
        }
      ],
      title: {
        text: this.symbol + " Historical"
      },
      subtitle: {
        text: 'With SMA and Volume by Price technical indicators'
      },
      yAxis: [
        {
          labels: {
            align: 'right',
            x: -3
          },
          title: {
            text: 'OHLC'
          },
          height: '60%',
          lineWidth: 2,
          startOnTick: false,
          endOnTick: false,
          resize: {
            enabled: true
          }
        },
        {
          labels: {
            align: 'right',
            x: -3
          },
          title: {
            text: 'Volume'
          },
          top: '65%',
          height: '35%',
          offset: 0,
          lineWidth: 2
        }
      ],
      tooltip: {
        split: true
      },
      rangeSelector: {
        buttons: [
          {
            type:'month',
            count: 1,
            text:'1m'
          },
          {
            type:'month',
            count: 3,
            text:'3m'
          },
          {
            type:'month',
            count: 6,
            text:'6m'
          },
          {
            type:'ytd',
            text:'YTD'
          },
          {
            type:'year',
            count: 1,
            text:'1y'
          },
          {
            type:'all',
            text:'All'
          }
        ],
        selected: 2
      },
      time: {
        timezoneOffset: new Date().getTimezoneOffset()
      }
    };
  }


  createHourlyChart() {

    let data = this.hourlyCandle?.t.map((val, index) => {
      return [val*1000, this.hourlyCandle?.c[index]];
    });

    console.log(data);

    this.hourlyChartOptions = {
      series:[
        {
          data: data,
          color: this.quote!.d<0 ? '#FF0000': '#28A745',
          showInNavigator: false,
          name: this.profile2?.ticker,
          type: 'line',
          tooltip: {
            valueDecimals: 2
          }
        }
      ],
      title: {
        text: this.profile2?.ticker + " Hourly Price Variation"
      },
      rangeSelector: {
        enabled: false
      },
      navigator: {
        enabled: false
      },
      time: {
        timezoneOffset: new Date().getTimezoneOffset()
      }
    }
  }

  formattedDateTime(date:Date) {
    return date.getFullYear()+"-"+(date.getMonth() +1)+"-"+date.getDate()+" "+date.toLocaleTimeString('en-GB');
  }

  checkExist(ticker: string) {
    this.backendHelper.getProfile2(ticker).subscribe(
      (values) => {
        this.profile2 = values;
        if(values.ticker) {
          this.validTicker = true;
          this.fetchAll(ticker);
        } else {
          this.validTicker = false;
        }
      }
    );
  }

  fetchAll(ticker: string) {
    this.backendHelper.getQuote(ticker).subscribe(
      (values) => {
        this.quote = values;
        this.closedTime = this.formattedDateTime(new Date(values.t*1000));
        this.currentTime = this.formattedDateTime(new Date());
        
        let diff = Math.abs(new Date().getTime()-values.t*1000);
        if( diff <= 60000) {
          this.marketOpen = true;
        } else {
          this.marketOpen = false;
        }
        this.backendHelper.getCandle(ticker,'5',values.t-21600,values.t).subscribe(
          (values) => {
            this.hourlyCandle = values;
            this.createHourlyChart();
          }
        );
      }
    );

    this.backendHelper.getCandle(ticker,'D', 
      Math.floor(new Date().getTime()/1000-2*365*24*60*60), 
      Math.floor(new Date().getTime()/1000)).subscribe(
        (values) => {
          this.historicalCandle = values;
          this.createHistoricalChart();
        }
    );

    this.backendHelper.getNews(ticker).subscribe(
      (values) => {
        this.topNews = values.slice(0,20);
      }
    );

    this.backendHelper.getRecommendation(ticker).subscribe(
      (values) => {
        this.recommendation = values;
      }
    );

    this.backendHelper.getSocial(ticker).subscribe(
      (values) => {
        this.social = values;
      }
    );

    this.backendHelper.getPeers(ticker).subscribe(
      (values) => {
        this.peers = values;
      }
    );

    this.backendHelper.getEarnings(ticker).subscribe(
      (values) => {
        this.earnings = values;
      }
    );
  }

  clearData() {
    this.quote = undefined;
    this.profile2 = undefined;
    this.candle = undefined;
    this.peers = undefined;
    this.topNews = undefined;
    this.recommendation = undefined;
    this.social = undefined;
    this.earnings = undefined;
  }
}
