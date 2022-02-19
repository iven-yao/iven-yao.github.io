from flask import Flask, request, jsonify
from datetime import datetime
from dateutil.relativedelta import relativedelta
import requests

app = Flask(__name__)
api_key="c85otiaad3i9e9m10gk0"
finnhub_api = "https://finnhub.io/api/v1"

@app.route("/")
def root():
    return app.send_static_file('stocksearch.html')

@app.route("/trend", methods=["GET"])
def recommendation_trends():
    ticker_symbol = request.args.get("symbol","")
    payload = {"symbol":ticker_symbol, "token":api_key}
    trends = requests.get(finnhub_api+"/stock/recommendation", params=payload)
    return jsonify(trends.json())

@app.route("/profile", methods=["GET"])
def company_profile():
    ticker_symbol = request.args.get("symbol","")    
    payload = {"symbol":ticker_symbol, "token":api_key}
    profile = requests.get(finnhub_api+"/stock/profile2", params=payload)
    return jsonify(profile.json())

@app.route("/quote", methods=["GET"])
def quote():
    ticker_symbol = request.args.get("symbol","")
    payload = {"symbol":ticker_symbol, "token":api_key}
    quote_data = requests.get(finnhub_api+"/quote", params=payload)
    return jsonify(quote_data.json())

@app.route("/candle", methods=["GET"])
def candle():
    ticker_symbol = request.args.get("symbol","")
    now = datetime.now()
    from_date = now-relativedelta(months=6, days=1)
    unix_now = int(now.timestamp())
    unix_from = int(from_date.timestamp())
    payload = {"symbol":ticker_symbol, "resolution":"D", "from":unix_from, "to":unix_now, "token": api_key}
    candle_data = requests.get(finnhub_api+"/stock/candle",params=payload)
    return jsonify(candle_data.json())

@app.route("/news", methods=["GET"])
def news():
    ticker_symbol = request.args.get("symbol","")
    now = datetime.now()
    from_date = now-relativedelta(months=1)
    format_now = now.strftime("%Y-%m-%d")
    format_from = from_date.strftime("%Y-%m-%d")
    payload = {"symbol":ticker_symbol, "from": format_from, "to": format_now, "token":api_key}
    latest_news = requests.get(finnhub_api+"/company-news", params=payload)
    return jsonify(latest_news.json())
