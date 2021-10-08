module.exports = {
  presets: [
    [
      '@vue/app',
      {
        useBuiltIns: 'entry'
      },
    ],
  ],
  "plugins": [
     "@babel/plugin-transform-runtime",
     ["import", {
      "libraryName": "view-design",
      "libraryDirectory": "src/components"
    }
   ]
  ],
}
