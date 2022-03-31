import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder} from '@angular/forms';
import { Observable } from 'rxjs';
import { switchMap, debounceTime, tap, finalize, map, startWith } from 'rxjs/operators';
import { Router } from '@angular/router';
import { BackendHelperService } from '../backend-helper.service';
import { SearchDAO } from '../dao/search-dao';
import { CandleDAO } from '../dao/candle-dao';
import { EarningsDAO } from '../dao/earnings-dao';
import { NewsDAO } from '../dao/news-dao';
import { Profile2DAO } from '../dao/profile2-dao';
import { QuoteDAO } from '../dao/quote-dao';
import { RecommendationDAO } from '../dao/recommendation-dao';
import { SocialDAO } from '../dao/social-dao';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})


export class SearchComponent implements OnInit {

  searchResults: SearchDAO = {} as SearchDAO;
  searchValue:string|undefined;
  errorMsg!:string;
  isLoading:boolean = false;
  searchForm: FormGroup;
  emptyInput:boolean = false;
  
  constructor(
    private formBuilder: FormBuilder,
    private backendHelper: BackendHelperService,
    private router: Router
  ) {
    this.searchForm = this.formBuilder.group({searchValue:''});
  }

  ngOnInit(): void {
    
    this.searchForm.get('searchValue')?.valueChanges.pipe(
      debounceTime(500),
      tap(()=>{
        this.errorMsg='';
        this.isLoading=true;
        this.searchResults={} as SearchDAO;
      }),
      switchMap((value) => (this.backendHelper.getSearch(value)
        .pipe(
          finalize(()=>{this.isLoading=false;})))) 
    ).subscribe((results:any) => {
      this.searchResults = results;
    });
  }

  onSubmit(form: any) {
    // console.log('searching symbol: ', form['searchValue']);
    if(form['searchValue'] == undefined || form['searchValue'] == '') {
      this.emptyInput = true;
    } 
    else {
      this.router.navigateByUrl('/search/'+form['searchValue'].toUpperCase());
      this.searchForm.reset();
      this.emptyInput = false;
    } 
  }

  clearInput() {
    this.searchForm.reset();
  }

  dismissAlert() {
    this.emptyInput = false;
  }

}
