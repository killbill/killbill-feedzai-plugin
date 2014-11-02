killbill-feedzai-plugin
=======================

Kill Bill fraud plugin based on the Feedzai API. The plugin uses the open source  [Feedzai client library](https://github.com/killbill/feedzai-client). The plugin will intercept the payment calls prior doing the authorization and score the payment. If the score is too high (likely fraudulent), the authorization gets aborted, and the checkout flow can be modified accordingly.

The plugin was showcased during the Money20/20 Hackathon:

Of the five global regions, Latin America has the fastest growing internet population, increasing 12 percent in the past year to more than 147 million unique visitors in March 2013. Demand for online payments is huge.

Support for local debit and credit cards is poor however and because of the high proportion of unbanked, most shoppers in LatAm have to rely on cash-based payment methods, such as the Boleto. These became quite sophisticated over the years and even attractive: costs and risks for merchants are reduced (no chargeback) and shoppers have the ability pay at any time, in their own neighborhood.

Because fraud is still predominant in credit card transactions, simply using a risk management provider, such as Feedzai, to block sign-ups for online subscription services can potentially lead to a loss in revenue because of false positive transactions. In our demo, we show how we can leverage both Feedzai and cash-based payment methods on top of the Kill Bill platform, to prevent fraud, while mitigating the risk of blocking legitimate users. And because of the predominance of such alternative payment methods today, the proposed changes in the checkout flow add only an acceptable amount of friction.




