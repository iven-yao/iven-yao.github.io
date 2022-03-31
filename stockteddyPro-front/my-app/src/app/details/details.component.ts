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
  peers:[]|undefined;
  topNews:NewsDAO[]|undefined;
  recommendation: RecommendationDAO[]|undefined;
  social: SocialDAO|undefined;
  earnings: EarningsDAO[]|undefined;
  
  Highcharts = Highcharts;
  hourlyChartOptions: Options = {} as Options;

  constructor(
    private route: ActivatedRoute,
    private backendHelper: BackendHelperService,
    private newsModalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.clearData();
      this.symbol = params.get('ticker')??' ';
      this.checkExist(this.symbol);
    });
  }

  openNewsDetail(news: NewsDAO){
    const newsModal = this.newsModalService.open(NewsComponent);
    newsModal.componentInstance.news = news;
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
        this.backendHelper.getCandle(ticker,5,values.t-21600,values.t).subscribe(
          (values) => {
            this.hourlyCandle = values;
            this.createHourlyChart();
            console.log(values);
          }
        );
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
