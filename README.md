##Introduction
Currency exchange involves converting one currency to another, which have often
varying exchange rates. This in turn could cause arbitrage—when you can convert and
exchange through multiple currencies and end up more money than that starting point.
For instance: if we had a cycle like NZD -> JPY -> AUD -> NZD, and the cycle get you from
$1 to $1.05. For this task I chose Bellman-Ford as it provided straightforward tracking of
arbitrage cycle tracking. For this task I will be completing arbitrage detection and best
conversion rates, and the cycle detection. As for API data, I will be using Exchange-Rate
API, because it lets you retrieve real time currency exchange rates, which can further
help solidify the effectiveness of the program in real scenarios.

##Aim of program
This project makes use of the Bellman-Ford algorithm to help detect the negative cycle and
the best conversion rates between currencies.

##How it detects arbitrage
A negative cycle is detected when the sum of edges along a certain cycle is negative. In
the case of my currency graph, after applying -log transformation to the exchange rates,
which are input by either a text file or API parsed data. If the algorithm detects a negative
cycle than the original value, then arbitrage exists. There’d need to be list of distances
and predecessors, to make it possible to detect. If any edges can be improved a
negative cycle exists.

##Best conversion path
For cases when there is no arbitrage, the program can still provide value by outputting
the best conversion between currencies A and B. Similarly to a negative cycle detection,
Bellman-Ford can be used again—to detect the best conversion path. In this case
there’ll be a slight difference in the methodology but similar ideas.