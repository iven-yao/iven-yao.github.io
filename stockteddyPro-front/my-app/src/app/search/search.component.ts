import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder} from '@angular/forms';
import { Observable } from 'rxjs';
import { switchMap, debounceTime, tap, finalize, map, startWith } from 'rxjs/operators';
import { Router } from '@angular/router';
import { BackendHelperService } from '../backend-helper.service';
import { SearchDAO } from '../dao/search-dao';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})


export class SearchComponent implements OnInit {

  searchResults: SearchDAO = {} as SearchDAO;
  searchValue:string = '';
  errorMsg!:string;
  isLoading:boolean = false;
  searchForm: FormGroup;

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

  onSubmit(symbol: any) {
    console.log('searching symbol: ', symbol['searchValue']);
    this.router.navigateByUrl('/details/'+symbol['searchValue']);
    this.searchForm.reset();
  }

  clearInput() {
    this.searchForm.reset();
  }

}
