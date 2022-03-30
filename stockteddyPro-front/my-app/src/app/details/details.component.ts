import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendHelperService } from '../backend-helper.service';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  symbol:string = '';
  constructor(
    private route: ActivatedRoute,
    private backendHelper: BackendHelperService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.symbol = params.get('symbol')??'';
    });

    this.backendHelper.getProfile2(this.symbol).subscribe(
      (values) => {
        console.log('profile2');
        console.log(values);
      }
    );

    this.backendHelper.getCandle(this.symbol).subscribe(
      (values) => {
        console.log('candle');
        console.log(values);
      }
    );

    this.backendHelper.getQuote(this.symbol).subscribe(
      (values) => {
        console.log('quote');
        console.log(values);
      }
    );

    this.backendHelper.getNews(this.symbol).subscribe(
      (values) => {
        console.log('news');
        console.log(values);
      }
    );

    this.backendHelper.getRecommendation(this.symbol).subscribe(
      (values) => {
        console.log('recommendation');
        console.log(values);
      }
    );

    this.backendHelper.getSocial(this.symbol).subscribe(
      (values) => {
        console.log('social');
        console.log(values);
      }
    );

    this.backendHelper.getPeers(this.symbol).subscribe(
      (values) => {
        console.log('peers');
        console.log(values);
      }
    );

    this.backendHelper.getEarnings(this.symbol).subscribe(
      (values) => {
        console.log('earnings');
        console.log(values);
      }
    );

    console.log(this.symbol);
  }

}
