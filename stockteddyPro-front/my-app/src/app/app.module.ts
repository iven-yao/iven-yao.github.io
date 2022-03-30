import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Routes, RouterModule} from '@angular/router';
import { MatAutocompleteModule} from '@angular/material/autocomplete';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { AppComponent } from './app.component';
import { TopNavComponent } from './top-nav/top-nav.component';
import { FooterComponent } from './footer/footer.component';
import { SearchComponent } from './search/search.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { BackendHelperService } from './backend-helper.service';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DetailsComponent } from './details/details.component';


const appRoutes: Routes = [
  { path: '', component: SearchComponent},
  { path: 'watchlist', component: WatchlistComponent},
  { path: 'portfolio', component: PortfolioComponent},
  { path: 'details/:symbol', component: DetailsComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    TopNavComponent,
    FooterComponent,
    SearchComponent,
    WatchlistComponent,
    PortfolioComponent,
    DetailsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    HttpClientModule,
    RouterModule.forRoot(
      appRoutes
      // ,{useHash: true}
    ),
    BrowserAnimationsModule,
    NgbModule,
    MatProgressSpinnerModule
  ],
  providers: [BackendHelperService],
  bootstrap: [AppComponent]
})
export class AppModule { }
